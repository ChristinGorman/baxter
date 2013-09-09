package no.gorman.please.common;

import no.gorman.database.Column;
import no.gorman.database.DatabaseColumns;

import java.util.*;

public class GrownUp implements NameMethods {

    @Column(column=DatabaseColumns.grownup_id)
    private Long grownup_id;

    @Column(column=DatabaseColumns.grownup_first_name)
    private String grownup_first_name;

    @Column(column=DatabaseColumns.grownup_middle_name)
    private String grownup_middle_name;

    @Column(column=DatabaseColumns.grownup_last_name)
    private String grownup_last_name;

    @Column(column=DatabaseColumns.telephone)
    private String telephone;

    @Column(column=DatabaseColumns.email)
    private String email;

    @Column(column=DatabaseColumns.password)
    private String password;

    @Column(column=DatabaseColumns.grownup_daycare_id)
    private Long grownup_daycare_id;

    @Column(column=DatabaseColumns.daycare_name)
    private String daycare_name;

    @Column(column=DatabaseColumns.grownup_version)
    private int grownup_version = 0;

    private Set<String> club_id = new HashSet<>();

    public Long getGrownUpId() {
        return grownup_id;
    }

    public void setGrownUpId(Long grownUpId) {
        grownup_id = grownUpId;
    }

    @Override
    public String getFirstName() {
        return grownup_first_name;
    }

    public void setFirstName(String grownUpFirstName) {
        grownup_first_name = grownUpFirstName;
    }

    @Override
    public String getMiddleName() {
        return grownup_middle_name;
    }

    public void setMiddleName(String grownUpMiddleName) {
        grownup_middle_name = grownUpMiddleName;
    }

    @Override
    public String getLastName() {
        return grownup_last_name;
    }

    public void setLastName(String grownUpLastName) {
        grownup_last_name = grownUpLastName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getDayCareId() {
        return grownup_daycare_id;
    }

    public void setDayCareId(Long dayCareCenterId) {
        this.grownup_daycare_id = dayCareCenterId;
    }

    public String getDayCareName() {
        return daycare_name;
    }

    public void setVersion(int version){
        this.grownup_version = version;
    }

    public int getVersion() {
        return grownup_version;
    }

    @Override
    public String getNickname() {
        return getFirstName();
    }

    public void addClubs(Collection<String> clubNames) {
        this.club_id.addAll(clubNames);
    }
}