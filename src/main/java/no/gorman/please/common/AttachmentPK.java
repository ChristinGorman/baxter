package no.gorman.please.common;

import no.gorman.database.Column;
import no.gorman.database.DatabaseColumns;

public class AttachmentPK {

    @Column(column = DatabaseColumns.attachment_id)
    private Long attachment_id;

    public Long getAttachmentId() {
        return attachment_id;
    }

    @Override
    public String toString(){
        return String.valueOf(attachment_id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttachmentPK other = (AttachmentPK) o;

        if (attachment_id != null ? !attachment_id.equals(other.attachment_id) : other.attachment_id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = attachment_id != null ? attachment_id.hashCode() : 0;
        return result;
    }
}