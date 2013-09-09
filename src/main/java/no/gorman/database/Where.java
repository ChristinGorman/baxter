package no.gorman.database;

public class Where {

    public final DatabaseColumns column;
    public final String operator;
    public final Object value;

    public Where(DatabaseColumns column, String operator) {
        this.column = column;
        this.operator = operator;
        this.value = null;
    }

    public Where(DatabaseColumns column, String operator, Object value) {
        this.column = column;
        this.operator = operator;
        this.value = value;
    }
    
    @Override
    public String toString() {
        return column.getTable() + "." + column.name() + " " + operator + " " + value;
    }
}
