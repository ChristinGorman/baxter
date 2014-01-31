package no.gorman.please.timeline;

import no.gorman.database.*;
public class Attachment {

    @Column(column=DatabaseColumns.attachment_id)
    private Long attachment_id;

    @Column(column=DatabaseColumns.content_type)
    private String content_type;

    @Column(column=DatabaseColumns.attachment)
    private byte[] attachment;

    @Column(column=DatabaseColumns.thumbnail)
    private byte[] thumbnail;

    @Column(column=DatabaseColumns.attachment_event_id)
    private long attachment_event_id;

    public Attachment() {}

    public Attachment(long eventId, String contentType, byte[] file) {
        this.attachment_event_id = eventId;
        this.content_type = contentType;
        this.attachment = file;
    }

    public byte[] getAttachment() {
        return attachment;
    }

    public void setThumbnail(byte[] thumb) {
        this.thumbnail = thumb;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public String getContentType() {
        return content_type;
    }
}
