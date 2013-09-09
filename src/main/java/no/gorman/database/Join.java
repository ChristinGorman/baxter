package no.gorman.database;

import java.util.Objects;

public class Join {

    public final DatabaseColumns primary;
    public final DatabaseColumns foreign;

    public Join(DatabaseColumns primary, DatabaseColumns foreign) {
        this.primary = primary;
        this.foreign = foreign;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Join))
            return false;
        Join other = (Join) obj;
        return Objects.equals(primary, other.primary) && Objects.equals(foreign, other.foreign);
    }

    @Override
    public int hashCode() {
        return Objects.hash(primary, foreign);
    }

    @Override
    public String toString() {
        return primary.name() + " = " + foreign.name();
    }
}
