package no.gorman.please.overview;

import no.gorman.database.*;
import java.time.*;

public class Schedule {

    @Column(column=DatabaseColumns.schedule_id)
    private Long schedule_id;

    @Column(column=DatabaseColumns.schedule_child_id)
    private Long schedule_child_id;

    @Column(column=DatabaseColumns.last_event)
    private Long last_event;

    @Column(column=DatabaseColumns.interval)
    private Integer interval;

    @Column(column=DatabaseColumns.schedule_name)
    private String schedule_name;


    public void setChildId(Long childId) {
        this.schedule_child_id = childId;
    }

    public void setLastEventTime(Long eventTime) {
        this.last_event = eventTime;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public void setName(String name) {
        this.schedule_name = name;
    }
}
