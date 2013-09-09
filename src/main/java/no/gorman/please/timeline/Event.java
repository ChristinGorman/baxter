package no.gorman.please.timeline;

import no.gorman.database.*;
import no.gorman.please.common.Child;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Event {

    @Column(column=DatabaseColumns.event_id)
    private Long event_id;

    @Column(column=DatabaseColumns.event_name)
    private String event_name;

    @Column(column=DatabaseColumns.event_time)
    private Long event_time;

    private final List<Long> attachments = new ArrayList<>();
    private final List<ChildName> children = new ArrayList<>();

    public Long getEventId() {
        return event_id;
    }

    public String getEventName() {
        return event_name;
    }

    public void setEventName(String event_name) {
        this.event_name = event_name;
    }

    public void setEventTime(Long event_time) {
        this.event_time = event_time;
    }

    public void addChildren(Collection<ChildName> children) {
        this.children.addAll(children);
    }

    public void addAttachments(Collection<Long> attachmentIds) {
        this.attachments.addAll(attachmentIds);
    }

    public static Event withNameAndTime(String eventName, long timestamp) {
        Event event = new Event();
        event.setEventTime(System.currentTimeMillis());
        event.setEventName(eventName);
        return event;
    }
}
