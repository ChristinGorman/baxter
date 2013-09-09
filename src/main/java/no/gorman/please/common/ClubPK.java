package no.gorman.please.common;

import no.gorman.database.Column;
import no.gorman.database.DatabaseColumns;

public class ClubPK {

    @Column(column = DatabaseColumns.club_id)
    private Long club_id;

    public ClubPK() {

    }
    public ClubPK(long clubId) {
        this.club_id = clubId;
    }

    public Long getId() {
        return club_id;
    }

    @Override
    public String toString(){
        return String.valueOf(club_id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClubPK other = (ClubPK) o;

        if (club_id != null ? !club_id.equals(other.club_id) : other.club_id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = club_id != null ? club_id.hashCode() : 0;
        return result;
    }
}