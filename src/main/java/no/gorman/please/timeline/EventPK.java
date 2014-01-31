package no.gorman.please.timeline;

import no.gorman.database.Column;
import no.gorman.database.DatabaseColumns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EventPK {

    @Column(column=DatabaseColumns.event_id)
    private Long event_id;

    public EventPK() {
    }

    public EventPK(Long eventId) {
        this.event_id = eventId;
    }
}
