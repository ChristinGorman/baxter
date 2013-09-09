package no.gorman.please.timeline;


import no.gorman.database.Column;
import no.gorman.database.DatabaseColumns;

import java.util.Objects;

public class ChildName {

    @Column(column=DatabaseColumns.child_id)
    private Long child_id;

    @Column(column=DatabaseColumns.nickname)
    private String nickname;

    public Long getChildId() {
        return child_id;
    }

    @Override
    public String toString() {
        return nickname;
    }

    @Override
    public int hashCode() {
        return Objects.hash(child_id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        return Objects.equals(((ChildName)obj).child_id, child_id);
    }
}