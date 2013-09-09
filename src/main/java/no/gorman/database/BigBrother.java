package no.gorman.database;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static no.gorman.database.DBFunctions.get;
import static no.gorman.database.DBFunctions.getMappedFields;
import static no.gorman.database.DBFunctions.getPrimaryKeyField;

public class BigBrother {

    public static interface Spy { void suspectAltered(Object suspect); }

    private static final ConcurrentMap<RowIdentifier, WeakHashMap<Spy, Boolean>> spies = new ConcurrentHashMap<>();
    private static List<WeakReference<Spy>> spyRefs = Collections.synchronizedList(new ArrayList<>());
    //Keep track of all weak references created, listen for when they are garbage collected,
    // so the main spy-map doesn't fill up with keys that have no living spies left.
    private static final ReferenceQueue<Spy> terminatedSpies = new ReferenceQueue<>();

    static {
        new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        terminatedSpies.remove();
                        spyRefs.removeIf(spy -> spy.get() == null);
                        spies.entrySet().removeIf(entry -> entry.getValue().isEmpty());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
    }

    public static Optional<Set<Spy>> getSpies(RowIdentifier key) {
        if (spies.containsKey(key)) {
            return Optional.of(spies.get(key).keySet());
        } else {
            return Optional.empty();
        }
    }

    public static void inform(RowIdentifier rowIdentifier, Object suspect) {
        getSpies(rowIdentifier).orElse(new HashSet<>()).forEach(spy -> spy.suspectAltered(suspect));
    }

    public static void informAllAgents(Object... suspects) {
        for (Object suspect : suspects) {
            findWhoMightBeInterested(suspect).forEach(row -> inform(row, suspect));
        }
    }

    public static void spyOn(Object suspect, Spy spy) {
        for (RowIdentifier key : findRowsToSpyOn(suspect)) {
            if (!spies.containsKey(key)) {
                spies.put(key, new WeakHashMap<>());
            }
            spyRefs.add(new WeakReference<>(spy, terminatedSpies));
            spies.get(key).put(spy, true);
        }
    }

    /**
     * Example:
     * findRowsToSpyOn(<child with child_id=42>) would return the
     * primary key row of the child table: child_id = 42,
     * plus the foreign references referring to this entry. For example
     * the Schedule table's schedule_child_id=42
     *
     */
    public static <T> Collection<RowIdentifier> findRowsToSpyOn(T suspect) {
        List<RowIdentifier> keys = new ArrayList<>();
        Field pkField = getPrimaryKeyField(suspect);
        Object suspectId = get(pkField, suspect);
        DatabaseColumns pkColumn = pkField.getAnnotation(Column.class).column();
        keys.add(new RowIdentifier(pkColumn, suspectId));
        DatabaseColumns.incomingReferenceColumns(pkColumn.getTable()).forEach(col -> keys.add(new RowIdentifier(col, suspectId)));
        return keys;
    }

    /**
     * Example:
     * findWhoMightBeInterested(<child with child_id=23 and child_daycare_id=2>) would return the
     * primary key row of the child table: child_id=23, and would also
     * return rows this child "belongs to". Like the day care center with daycare_id=2.
     * It won't return any other fields than what the type <T> actually has defined.
     * If T is a child-class, but does not specify any link to the daycare center,
     * those listening for updates on the daycare center won't be notified.
     * Could fix this by querying the database from here, will maybe have to do this later,
     * but haven't had the need to so far.
     */
    public static <T> Collection<RowIdentifier> findWhoMightBeInterested(T suspect) {
        List<RowIdentifier> keys = new ArrayList<>();
        Field pkField = getPrimaryKeyField(suspect);
        Object suspectId = get(pkField, suspect);
        keys.add(new RowIdentifier(pkField.getAnnotation(Column.class).column(), suspectId));
        for (Field f : getMappedFields(suspect.getClass())){
            DatabaseColumns col = f.getAnnotation(Column.class).column();
            if (col.getType() == ColumnType.ForeignKey){
                keys.add(new RowIdentifier(col, get(f, suspect)));
            }
        }
        return keys;
    }

    public static class RowIdentifier {

        public final Object value;
        public final DatabaseColumns column;

        public RowIdentifier(DatabaseColumns column, Object value) {
            this.column = column;
            this.value = value;
        }

        @Override
        public String toString() {
            return column.name() + "." + value;
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null
                    && (obj instanceof RowIdentifier)
                    && column == ((RowIdentifier) obj).column
                    && Objects.equals(value, ((RowIdentifier) obj).value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(column, value);
        }
    }
}
