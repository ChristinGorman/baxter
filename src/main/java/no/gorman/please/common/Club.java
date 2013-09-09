package no.gorman.please.common;

import no.gorman.database.Column;
import no.gorman.database.DatabaseColumns;

import java.util.Objects;

import static no.gorman.database.DatabaseColumns.club_id;

public class Club {
    @Column(column=DatabaseColumns.club_id)
    private Long club_id;

    @Column(column=DatabaseColumns.club_name)
    private String club_name;

    @Column(column=DatabaseColumns.club_color)
    private String club_color;

    @Column(column= DatabaseColumns.club_daycare_id)
    private Long club_daycare_id;

    public Long getClubId() {
        return club_id;
    }
    public static Club withName(String name) {
        Club c = new Club();
        c.club_name = name;
        return c;
    }

    @Override
    public boolean equals(Object o){
        if (o == null) return false;
        if (! ( o instanceof Club))return false;
        return Objects.equals(this.club_id, ((Club) o).club_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(club_id);
    }
}
