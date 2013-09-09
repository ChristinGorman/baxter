package no.gorman.please.common;

import no.gorman.database.Column;
import no.gorman.database.DatabaseColumns;

public class DayCareCenter {

    @Column(column=DatabaseColumns.daycare_id)
    private Long daycare_id;

    @Column(column=DatabaseColumns.daycare_name)
    private String daycare_name;

    public DayCareCenter() {
        
    }
    
    public DayCareCenter(String name) {
        daycare_name = name;
    }

    public Long getDayCareCenterId() {
        return daycare_id;
    }

    public String getDayCareName() {
        return daycare_name;
    }

    public void setDayCareName(String dayCareName) {
        daycare_name = dayCareName;
    }

    public static DayCareCenter withName(String name) {
        DayCareCenter newOne = new DayCareCenter();
        newOne.setDayCareName(name);
        return newOne;
    }
}
