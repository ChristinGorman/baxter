package no.gorman.please.overview;


import no.gorman.database.Column;
import no.gorman.database.DatabaseColumns;
import no.gorman.please.common.NameMethods;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class OverviewOfChild implements NameMethods {

    public OverviewOfChild() {

    }

    @Column(column=DatabaseColumns.child_id)
    private Long child_id;
    
    @Column(column=DatabaseColumns.dob)
    private LocalDate DOB;

    @Column(column=DatabaseColumns.child_first_name)
    private String child_first_name;

    @Column(column=DatabaseColumns.child_middle_name)
    private String child_middle_name;

    @Column(column=DatabaseColumns.child_last_name)
    private String child_last_name;

    @Column(column=DatabaseColumns.nickname)
    private String nickname;

    @Column(column=DatabaseColumns.child_version)
    private int child_version = 0;

    @Column(column=DatabaseColumns.color)
    private String color;

    @Column(column=DatabaseColumns.child_daycare_id)
    private Long child_daycare_id;

    private List<Schedule> schedules = new ArrayList<>();

    private List<String> groups = new ArrayList<>();

    public Long getChildId() {
        return child_id;
    }
    public void setChildId(Long childId) {
        this.child_id = childId;
    }
    public LocalDate getDOB() {
        return DOB;
    }
    public void setDOB(LocalDate dOB) {
        DOB = dOB;
    }

    @Override
    public String getFirstName() {
        return child_first_name;
    }
    public void setFirstName(String firstName) {
        this.child_first_name = firstName;
    }

    @Override
    public String getMiddleName() {
        return child_middle_name;
    }
    public void setMiddleName(String middleName) {
        this.child_middle_name = middleName;
    }

    @Override
    public String getLastName() {
        return child_last_name;
    }
    public void setLastName(String lastName) {
        this.child_last_name = lastName;
    }

    @Override
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public int getVersion() {
        return child_version;
    }
    public void setVersion(int version) {
        this.child_version = version;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }

    public Long getDaycareId() {
        return child_daycare_id;
    }

    public void setDaycareId(Long daycare) {
        this.child_daycare_id = daycare;
    }

    public void addSchedules(Collection<Schedule> schedules) {
        this.schedules.addAll(schedules);
    }

    public void addGroups(Collection<String> groupNames) {
        this.groups.addAll(groupNames);
    }

    @Override
    public String toString() {
        return nickname + "(" + child_version + ")";
    }


    @Override
    public int hashCode() {
        return Objects.hash(child_id, child_version);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OverviewOfChild other = (OverviewOfChild)obj;
        return Objects.equals(other.child_id, child_id) && (Objects.equals(other.child_version, child_version));
    }

    public static OverviewOfChild withNickname(String nick) {
        OverviewOfChild child = new OverviewOfChild();
        child.setNickname(nick);
        return child;
    }
}