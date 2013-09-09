package no.gorman.database;

public class OrderBy {

    public static final boolean ASCENDING = true;
    public static final boolean DESCENDING = false;

    private final DatabaseColumns column;
    private final boolean asc;

    public OrderBy(DatabaseColumns column) {
        this.column = column;
        this.asc = true;
    }

    public OrderBy(DatabaseColumns column, boolean ascending) {
        this.column = column;
        this.asc = ascending;
    }

    public String getOrder() {
        return asc ? "ASC" : "DESC";
    }

    public DatabaseColumns getColumn() {
        return column;
    }

}
