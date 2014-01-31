package no.gorman.please.timeline;

import no.gorman.database.*;
import no.gorman.please.common.AttachmentPK;
import no.gorman.please.utils.WithDatabase;
import no.gorman.please.common.ChildPK;
import no.gorman.please.common.ClubPK;
import org.apache.commons.fileupload.FileItem;

import java.util.*;
import java.util.stream.Stream;

import static java.lang.Long.parseLong;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static no.gorman.database.DatabaseColumns.*;
import static no.gorman.database.OrderBy.DESCENDING;
import static org.apache.commons.lang.StringUtils.*;

public class TimelineCRUD extends WithDatabase{

    public TimelineCRUD() {
    }

    public TimelineCRUD(DB db) {
        super(db);
    }

    public void insert(Event event, String[] childIds, String[] clubIds, Collection<FileItem> attachments) {
        boolean childrenSpecified = containsNumericData(childIds);
        boolean clubsSpecified = containsNumericData(clubIds);
        if (!childrenSpecified && !clubsSpecified) throw new IllegalArgumentException("No children or groups specified");

        getDB().insert(event);
        attachments.forEach(file -> {
            if (isNotBlank(file.getName())) { //why is this needed?
                getDB().insert(new Attachment(event.getEventId(), file.getContentType(), file.get()));
            }
        });

        Set<ChildPK> children = new HashSet<>();
        if (childrenSpecified) {
            Stream<ChildPK> pkStream = asList(childIds).stream().map(id -> new ChildPK(parseLong(id)));
            children.addAll((Collection<ChildPK>)pkStream.collect(toList()));
        }

        if (clubsSpecified) {
            List<ClubPK> clubs = asList(clubIds).stream().map(id -> new ClubPK(parseLong(id))).collect(toList());
            clubs.forEach(clubPk -> getDB().link(clubPk, event));
            children.addAll(getDB().select(ChildPK.class, new Where(grc_club_id, " IN (" + join(clubIds, ",") + ")")));
        }
        children.forEach(childPK -> getDB().link(childPK, event));
    }

    boolean containsNumericData(String[] ids) {
        String justNumbers = join(ids, "");
        return isNotBlank(justNumbers) && isNumeric(justNumbers);
    }

    public Event populate(Event e) {
        List<Long> attachments = getDB().select(attachment_id, Long.TYPE, new Where(attachment_event_id, "=", e.getEventId()));
        e.addAttachments(attachments);
        e.addChildren(getDB().select(ChildName.class,
                new Where(ec_event_id, "=", e.getEventId())));
        return e;
    }

    public List<Event> getEvents(long grownup_id) {
        return getDB()
                .select(Event.class, new OrderBy(event_time, DESCENDING), new Where(gc_grownup_id, " = ", grownup_id))
                .stream().map(this::populate).collect(toList());
    }

    public Attachment getAttachment(String idString, Long id) {
        return getDB().selectOnlyOne(
                Attachment.class,
                new Where(attachment_id, "=", id))
                .orElseThrow(() -> new IllegalArgumentException("No attachment found with id " + idString));
    }

    public Event getEvent(String eventId) {
        Optional<Event> event = getDB().selectOnlyOne(Event.class, new Where(event_id, " = ", Long.parseLong(eventId)));
        return event.isPresent() ? populate(event.get()) : null;
    }

    public void deleteEvent(String eventId) {
        long id = Long.parseLong(eventId);
        EventPK eventPk = new EventPK(id);

        getDB().delete(AttachmentPK.class, new Where(attachment_event_id, " = ", id));

        getDB().select(ChildPK.class, new Where(ec_event_id, " = ", id)).forEach(c -> getDB().unlink(c, eventPk));
        getDB().select(ClubPK.class, new Where(ecl_event_id, " = ", id)).forEach(c -> getDB().unlink(c, eventPk));
        getDB().delete(EventPK.class, new Where(event_id, " = ", id));
    }
}
