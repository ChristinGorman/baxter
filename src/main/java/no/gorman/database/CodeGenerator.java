package no.gorman.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static no.gorman.database.DatabaseColumns.getColumnsFor;
import static org.apache.commons.lang.StringUtils.join;

public class CodeGenerator {

    public static void generateTableConstants(String tableName) throws Exception {

        Connection connection = DBFunctions.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM \""+ tableName + "\"");
        ResultSet rs = stmt.executeQuery();
        
        for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
            String col = rs.getMetaData().getColumnName(i+1);
            System.out.println(col + "(Table." + tableName + "),");
        }
        
    }
    public static void generateDO(String tableName) throws Exception {

        System.out.println("import no.gorman.database.*;");
        System.out.println("import java.time.*;");
        Connection connection = DBFunctions.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM \"" + tableName + "\"");
        ResultSet rs = stmt.executeQuery();
        @SuppressWarnings("serial")
        Map<String, String> types = new HashMap<String, String>() {{
            put("int8", "Long");
            put("char", "Boolean");
            put("varchar", "String");
            put("datetime2", "LocalDateTime");
            put("Date", "LocalDate");
            put("date", "LocalDate");
            put("timestamp", "LocalDateTime");
            put("numeric", "Double");
        }};
        for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
            String col = rs.getMetaData().getColumnName(i+1);
            String type = rs.getMetaData().getColumnTypeName(i+1);
            System.out.println("@Column(column=DatabaseColumns." + col + ")");
            String thisType = types.get(type);
            if (thisType == null) {
                thisType = type;
            }
            System.out.println("private " + thisType + " " + col + ";");
            System.out.println("");
        }
        
    }

    public static void createDB() {

        for (Table t : Table.values()) {
            System.out.println("CREATE TABLE " + t.name() + " (");
            List<String> colDefs = getColumnsFor(t).stream().map(col -> join(asList(col.name(), getDbType(col), getConstraints(col.getType())), " " )).collect(toList());
            System.out.println(join(colDefs, ",\n"));
            System.out.println(");");
            System.out.println("");
            System.out.println("ALTER TABLE public." + t.name() + " OWNER TO postgres;");
            System.out.println("");
            Optional<DatabaseColumns> pk = DatabaseColumns.getPrimaryKey(t);
            if (pk.isPresent()) {
                String seq = t.name() + "_" + pk.get().name() + "_SEQ";
                System.out.println("CREATE SEQUENCE " + seq);
                System.out.println("START WITH 1");
                System.out.println("INCREMENT BY 1");
                System.out.println("NO MINVALUE");
                System.out.println("NO MAXVALUE");
                System.out.println("CACHE 1;");
                System.out.println("");
                System.out.println("ALTER TABLE public." + seq + " OWNER TO postgres;");
                System.out.println("ALTER SEQUENCE " + seq + " OWNED BY " + t.name() + "." + pk.get().name() + ";");
                System.out.println("ALTER TABLE ONLY " + t.name() + " ALTER COLUMN " + pk.get().name() + " SET DEFAULT nextval('" + seq + "'::regclass);");
                System.out.println("ALTER TABLE ONLY " + t.name() + " ADD CONSTRAINT " + pk.get().name() + " PRIMARY KEY (" + pk.get().name() + ");");
            }else {
                System.out.println("ALTER TABLE ONLY " + t.name() + " ADD CONSTRAINT " + t.name() + "_pkey PRIMARY KEY (" + join(getColumnsFor(t).stream().filter(col -> col.getType() == ColumnType.ForeignKey).collect(toList()), ", ") + ");");
            }
            System.out.println("");
        }
        System.out.println("");
        for (DatabaseColumns col : DatabaseColumns.values()) {
            if (col.getType() == ColumnType.ForeignKey){
                System.out.println("CREATE INDEX FKI_" + col.name() + "FK ON " + col.getTable().name() + " USING btree (" + col.name() + ");");
                System.out.println("ALTER TABLE ONLY " + col.getTable().name() + " ADD CONSTRAINT " + col.name() + "FK FOREIGN KEY (" + col.name() + ") REFERENCES " + col.getJoinedTo().name() + "(" + DatabaseColumns.getPrimaryKey(col.getJoinedTo()).get().name() + ");");
            }
        }
    }

    private static String getDbType(DatabaseColumns column) {
        if (String.class.equals(column.getFieldClass()))
            return "character varying(" + column.getDataLength() + ")";
        if (asList(Integer.class, Integer.TYPE, Long.class, Long.TYPE).contains(column.getFieldClass()))
            return "bigint";
        if (LocalDate.class.equals(column.getFieldClass()))
            return "bigint";
        if (byte[].class.equals(column.getFieldClass()))
            return "bytea";
        return "";

    }

    private static String getConstraints(ColumnType type) {
        switch(type){
            case PrimaryKey: return "NOT NULL";
            case Version: return "DEFAULT 0 NOT NULL";
            default:
                return  "";
        }
    }


    public static void main(String[] args) throws Exception {
//        DBFunctions.setupConnectionPool("jdbc:postgresql://localhost/bax?useUnicode=true&characterEncoding=utf8", "postgres", "baxter", 3);
//        generateDO(Table.club.name());
        createDB();
    }
}

