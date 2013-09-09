package no.gorman.please.common;

import no.gorman.database.Column;
import no.gorman.database.DatabaseColumns;

public class ChildPK {

    @Column(column = DatabaseColumns.child_id)
    private Long child_id;

    public ChildPK() {

    }

    public ChildPK(long id) {
        this.child_id = id;
    }

    public Long getChildId() {
        return child_id;
    }


    @Override
    public String toString(){
        return String.valueOf(child_id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChildPK childPK = (ChildPK) o;

        if (child_id != null ? !child_id.equals(childPK.child_id) : childPK.child_id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = child_id != null ? child_id.hashCode() : 0;
        return result;
    }
}