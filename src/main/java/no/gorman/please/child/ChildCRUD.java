package no.gorman.please.child;

import no.gorman.database.Where;
import no.gorman.please.common.Child;
import no.gorman.please.utils.WithDatabase;

import static no.gorman.database.DatabaseColumns.child_id;
import static no.gorman.database.DatabaseColumns.gc_grownup_id;

public class ChildCRUD extends WithDatabase {

    public void newChild(Child newOne) {
        getDB().insert(newOne);
    }

    public void updateChild(Child updated) {
        getDB().update(updated);
    }

    public Child findChild(long grownupId, int childId) {
        return getDB().selectOnlyOne(Child.class,
                new Where(child_id, " = ", childId),
                new Where(gc_grownup_id, " = ",grownupId)).orElseThrow(
                () -> new IllegalArgumentException("No permission / No child found with id " + childId));
    }
}