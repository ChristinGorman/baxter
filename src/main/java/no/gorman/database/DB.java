package no.gorman.database;

import no.gorman.please.common.Child;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static no.gorman.database.ColumnType.PrimaryKey;
import static no.gorman.database.ColumnType.Version;
import static no.gorman.database.DBFunctions.*;
import static org.apache.commons.lang.StringUtils.join;
import static org.apache.commons.lang.StringUtils.trim;

public class DB {

    private static final Logger log = LoggerFactory.getLogger(DB.class);


    private final Connection connection;
    final List<Runnable> onSuccessActions = new ArrayList<>(); //to be run when transaction completes successfully

    public DB(Connection connection) {
        this.connection = connection;
    }

    public <T> List<T> select(Class<T> clazz, Where... whereClause) {
        return select(clazz, new ArrayList<>(), whereClause);
    }

    public <T> List<T> select(Class<T> clazz, OrderBy orderBy, Where... whereClause) {
        return select(clazz, asList(orderBy), whereClause);
    }

    public <T> Optional<T> selectOnlyOne(Class<T> clazz, Where... where) {
        List<T> results = select(clazz, where);
        if (results.isEmpty()) return Optional.empty();
        if (results.size() > 1) throw new IllegalArgumentException("Expected only one value, but got " + results.size() + ". " + Arrays.toString(where));
        return Optional.of(results.get(0));
    }

    private <T> List<T> select(Class<T> clazz, List<OrderBy> orderBy, Where... whereClause) {
        String select = makeSelect(clazz, orderBy, whereClause);

        Set<Table> tables = getTables(clazz, whereClause);
        Collection<Join> joins = new ArrayList<>();
        if (tables.size() > 1) {
            joins.addAll(findJoins(tables));
            joins.forEach(join -> {
                tables.add(join.primary.getTable());
                tables.add(join.foreign.getTable());});
        }

        String from = " FROM " + join(tables, ", ");
        String where = makeWhere(joins, whereClause);
        String groupBy = makeGroupBy(clazz, orderBy);

        List<String> orderByStrings = orderBy.stream().map(by -> by.getColumn().name() + " " + by.getOrder()).collect(toList());
        String orderByStr = orderBy.isEmpty() ? "" : "ORDER BY " + join(orderByStrings, ",");

        String sql = join(asList(select, from, where, groupBy, orderByStr), " ");
        log.info(sql);
        return runSQL(clazz, sql, createParameterList(whereClause));
    }

    public <T> List<T> runSQL(Class<T> clazz, String sql, List<Object> parameters) {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            addParameters(stmt, parameters);
            try (ResultSet result = stmt.executeQuery()) {
                List<T> list = new ArrayList<>();
                while (result.next()) {
                    T instance = clazz.newInstance();
                    for (Field f : getMappedFields(clazz)) {
                        set(f, instance, getValueFromRS(result, getColumn(f)));
                    }
                    list.add(instance);
                }
                return list;
            }
        } catch (RuntimeException e) {
            log.error(sql);
            throw e;
        } catch (Exception e) {
            log.error(sql);
            throw new RuntimeException(e);
        }
    }


    public <T> List<T> select(DatabaseColumns column, Class<T> clazz, Where... whereClause) {

        if (!column.getFieldClass().isAssignableFrom(clazz)) throw new IllegalArgumentException(column + " is not of type  " + clazz.getSimpleName());
        String select = "SELECT DISTINCT " + column.name();
        Set<Table> tables = getTables(column.getFieldClass(), whereClause);
        tables.add(column.getTable());
        Collection<Join> joins = new ArrayList<>();
        if (tables.size() > 1) {
            joins.addAll(findJoins(tables));
            joins.forEach(join -> {tables.add(join.primary.getTable());tables.add(join.foreign.getTable());});
        }

        String from = " FROM " + join(tables, ",");
        String where = makeWhere(joins, whereClause);
        String sql = join(asList(trim(select), trim(from), trim(where)), " ").replace("  ", " ");
        log.info(sql);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            addParameters(stmt, createParameterList(whereClause));
            try (ResultSet result = stmt.executeQuery()) {
                List<T> list = new ArrayList<>();
                while (result.next()) {
                    list.add((T)getValueFromRS(result, column));
                }
                return list;
            }
        } catch (RuntimeException e) {
            log.error(sql);
            throw e;
        } catch (Exception e) {
            log.error(sql);
            throw new RuntimeException(e);
        }
    }

    public <T> void update(T updated) {
        update(updated, getMainTable(updated));
    }

    public <T> void update(final T updated, Table table) {
        List<Field> all = DBFunctions.getMappedFields(updated.getClass());

        Optional<Field> versionField = all.stream().filter(f -> getColumn(f).getType() == Version).findFirst();
        versionField.ifPresent(field -> {if (get(field, updated) == null) set(field, updated, 0);});

        List<Field> inMainTable = all.stream()
                .filter(f -> getColumn(f).getTable() == table)
                .filter(f -> getColumn(f).getType() != PrimaryKey)
                .collect(toList());
        List<String> setExpressions = inMainTable.stream()
                .map(f -> getColumn(f).name() + " = ?")
                .collect(Collectors.toList());
        List<Object> params = inMainTable.stream()
                .map(f -> (getColumn(f).getType() == Version) ? (1 + (int) get(f, updated)) : get(f, updated))
                .collect(toList());

        Field pk = all.stream().filter(f -> getColumn(f).getType() == PrimaryKey).findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot update " + updated.getClass().getName() + " as it has no primary key field"));

        String pkName = getColumn(pk).name();
        String sql = "UPDATE " + table.name() + " SET " + join(setExpressions, ", ") + " WHERE " + pkName + " = " + get(pk, updated);
        if (versionField.isPresent()) {
            String versionName = getColumn(versionField.get()).name();
            sql += " AND " + versionName + " = " + get(versionField.get(), updated);
        }
        log.info(sql);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            addParameters(stmt, params);
            if (stmt.executeUpdate() == 0) {
                String errorMsg = "Could not find row in table " + table + " with " + pkName + " = " + get(pk, updated);
                if (versionField.isPresent()) {
                    errorMsg += " and " + getColumn(versionField.get()) + " = " + get(versionField.get(), updated);
                    throw new ConcurrentModificationException(errorMsg);
                }
                throw new RuntimeException(errorMsg);
            }
            if (versionField.isPresent()) {
                set(versionField.get(), updated, (Integer) (get(versionField.get(), updated)) + 1);
            }
            onSuccessActions.add(() -> BigBrother.informAllAgents(updated));
        } catch (SQLException e) {
            log.error(sql);
            throw new RuntimeException(e);
        }
    }

    public void insert(Object... newOnes) {
        asList(newOnes).forEach(obj -> insert(obj, getMainTable(obj)));
    }

    public <T> Long insert(final T newInstance, Table table) {
        List<Field> all = DBFunctions.getMappedFields(newInstance.getClass());
        List<Field> inMainTable = all.stream()
                .filter(f -> getColumn(f).getTable() == table)
                .filter(f -> getColumn(f).getType() != PrimaryKey)
                .collect(toList());
        List<String> fieldNames = inMainTable.stream().map(f -> getColumn(f).name()).collect(toList());
        List<String> valueMarkers = fieldNames.stream().map(name -> "?").collect(toList());
        List<Object> params = inMainTable.stream()
                .map(f -> get(f, newInstance))
                .collect(toList());

        String sql = "INSERT INTO " + table.name() + "(" + join(fieldNames, ", ") + ") VALUES(" + join(valueMarkers, ", ") + ")";
        log.info(sql);
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            addParameters(stmt, params);
            stmt.execute();
            ResultSet newIdRS = stmt.getGeneratedKeys();
            newIdRS.next();
            long newId = newIdRS.getLong(1);
            Optional<Field> pk = all.stream().filter(f -> getColumn(f).getType() == PrimaryKey).findFirst();
            if (pk.isPresent()) {
                set(pk.get(), newInstance, newId);
            }
            onSuccessActions.add(() -> BigBrother.informAllAgents(newInstance));
            return newId;
        } catch (Exception e) {
            log.error(sql);
            throw new RuntimeException(e);
        }
    }

    public <T> void delete(Class<T> clazz, Where... where) {
        Table table = getMainTableForClass(clazz);
        Collection<T> deleted = select(clazz, where);
        Optional<DatabaseColumns> pk = DatabaseColumns.getPrimaryKey(table);
        String sql = "DELETE FROM " + table.name() + " " + makeWhere(new ArrayList<Join>(), where);
        log.info(sql);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            addParameters(stmt, createParameterList(where));
            stmt.execute();
            onSuccessActions.add(() -> BigBrother.informAllAgents(deleted.toArray()));
        } catch (Exception e) {
            log.error(sql);
            throw new RuntimeException(e);
        }
    }

    /**
     * Joins a with b via a many-to-many-table-entry.
     * Saves you having to create separate java objects representing each simple many-to-many-join.
     * <p/>
     * example:
     * <p/>
     * Child c = new Child(1);
     * GrownUp g = new GrownUp(2);
     * DB.link(c,g);
     * will first find a many-to-many-relationship table that links the two tables Child and GrownUp : GrownUpChild.
     * it will then run the following SQL-statement:
     * INSERT INTO GrownUpChild(gcChildId, gcGrownUpId) VALUES(1,2);
     *
     */
    public void link(Object from, Object to) {
        manyToManyOperation(from, to, "INSERT INTO %s (%s, %s) VALUES(?, ?)");
    }

    public void unlink(Object from, Object to) {
        manyToManyOperation(from, to, "DELETE FROM %s WHERE %s = ? AND %s = ?");
    }

    private void manyToManyOperation(Object from, Object to, String sql) {
        Table manyToMany = findManyToManyTable(from, to).orElseThrow(()-> new IllegalArgumentException("No mapping table found between " + from + " and " + to));
        DatabaseColumns fkFrom = DatabaseColumns.getForeignKey(getMainTable(from), manyToMany).orElseThrow(() -> new IllegalArgumentException("no foreign keys found for " + from ));
        DatabaseColumns fkTo = DatabaseColumns.getForeignKey(getMainTable(to), manyToMany).orElseThrow(() -> new IllegalArgumentException("no foreign keys found for " + to ));
        String filledOut = String.format(sql, manyToMany.name(),fkFrom.name(), fkTo.name());
        log.info(filledOut);

        try (PreparedStatement stmt = connection.prepareStatement(filledOut)) {
            Long fromId = (Long) getPrimaryKeyField(from).get(from);
            Long toId = (Long) getPrimaryKeyField(to).get(to);
            stmt.setLong(1, fromId);
            stmt.setLong(2, toId);
            stmt.executeUpdate();
            onSuccessActions.add(() -> BigBrother.inform(new BigBrother.RowIdentifier(fkFrom, fromId), to));
            onSuccessActions.add(() -> BigBrother.inform(new BigBrother.RowIdentifier(fkTo, toId), from));

        } catch (Exception e) {
            log.error(sql);
            throw new RuntimeException(e);
        }
    }

    public static void addParameters(PreparedStatement stmt, List<Object> params) {
        try {
            for (int i = 1; i <= params.size(); i++) {
                Object p = params.get(i-1);
                if (p == null) {
                    stmt.setObject(i, null);
                } else if (p instanceof String) {
                    stmt.setString(i, (String) p);
                } else if (p instanceof Integer) {
                    stmt.setInt(i, (Integer) p);
                } else if (p instanceof Long) {
                    stmt.setLong(i, (Long) p);
                } else if (p instanceof LocalDate) {
                    stmt.setInt(i, Integer.parseInt(((LocalDate) p).format(YYYY_MM_DD)));
                } else if (p.getClass().isEnum()) {
                    stmt.setString(i, ((Enum<?>) p).name());
                } else if (p.getClass() == byte[].class) {
                    stmt.setBytes(i, (byte[])p);
                } else {
                    throw new IllegalArgumentException("No mapping found for " + p);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getValueFromRS(ResultSet rs, DatabaseColumns col) throws IllegalAccessException, SQLException {
        Class<?> clazz = col.getFieldClass();
        String colName = col.name();
        if (clazz == String.class) {
            return rs.getString(colName);
        } else if (INT_TYPES.contains(clazz)) {
            int intValue = rs.getInt(colName);
            return (rs.wasNull() ? null : intValue);
        } else if (LONG_TYPES.contains(clazz)) {
            long longValue = rs.getLong(colName);
            return (rs.wasNull() ? null : longValue);
        } else if (clazz == LocalDate.class) {
            String dateString = String.valueOf(rs.getInt(colName));
            return (rs.wasNull() ? null : LocalDate.parse(dateString, YYYY_MM_DD));
        } else if (clazz == LocalDateTime.class) {
            Timestamp timestamp = rs.getTimestamp(colName);
            return (rs.wasNull() ? null : timestamp.toLocalDateTime());
        }else if (clazz == byte[].class) {
            byte[] array = rs.getBytes(colName);
            return rs.wasNull() ? null : array;
        } else if (clazz.isEnum()) {
            String enumVal = trim(rs.getString(colName));
            if (!rs.wasNull()) {
                try {
                    return Enum.valueOf((Class<? extends Enum>) clazz, enumVal);
                } catch (Exception e) {
                    log.error("Failed to load " + clazz + ": " + e.getMessage());
                }
            }
            return null;
        } else {
            throw new IllegalArgumentException("Damn it, didn't find any type for " + colName + " with type " + clazz);
        }
    }

    public void commitAndReleaseConnection() {
        try {
            if (connection == null) return;
            if (connection.isClosed()) return;
            connection.commit();
            onSuccessActions.forEach(runnable -> runnable.run());
            onSuccessActions.clear();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void rollback() {
        try {
            if (connection == null) return;
            if (connection.isClosed()) return;
            connection.rollback();
            onSuccessActions.clear();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
