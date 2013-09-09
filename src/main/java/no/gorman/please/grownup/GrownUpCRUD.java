package no.gorman.please.grownup;

import no.gorman.database.DB;
import no.gorman.database.Where;
import no.gorman.please.common.ChildPK;
import no.gorman.please.common.ClubPK;
import no.gorman.please.common.GrownUp;
import no.gorman.please.utils.WithDatabase;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static no.gorman.database.DatabaseColumns.*;
import static org.apache.commons.collections.CollectionUtils.subtract;

public class GrownUpCRUD extends WithDatabase {

    public void newGrownup(GrownUp newOne, String[] childIds, String[] clubIds) {
        DB db = getDB();
        db.insert(newOne);
        List<ChildPK> children = asList(childIds).stream().map(id -> new ChildPK(Long.parseLong(id))).collect(toList());
        children.forEach(child -> db.link(newOne, child));

        List<ClubPK> clubs = asList(clubIds).stream().map(id -> new ClubPK(Long.parseLong(id))).collect(toList());
        clubs.forEach(club -> db.link(newOne, club));
    }

    public void updateGrownup(GrownUp updated, String[] childIds, String[] clubIds) {
        DB db = getDB();
        db.update(updated);

        List<ChildPK> existing = db.select(ChildPK.class, new Where(gc_grownup_id, " = ", updated.getGrownUpId()));
        List<ChildPK> newOnes = asList(childIds).stream().map(id -> new ChildPK(Long.parseLong(id))).collect(toList());

        subtract(existing, newOnes).forEach(child -> db.unlink(updated, child));
        subtract(newOnes, existing).forEach(child -> db.link(updated, child));

        List<ClubPK> existingClubs = db.select(ClubPK.class, new Where(grg_grownup_id, " = ", updated.getGrownUpId()));
        List<ClubPK> newClubs = asList(clubIds).stream().map(id -> new ClubPK(Long.parseLong(id))).collect(toList());

        subtract(existingClubs, newClubs).forEach(club -> db.unlink(updated, club));
        subtract(newClubs, existingClubs).forEach(club -> db.link(updated, club));

    }

    public GrownUp getGrownup(int grownUpId) {
        return getDB().selectOnlyOne(GrownUp.class, new Where(grownup_id, " = ", grownUpId))
                .orElseThrow(() -> new IllegalArgumentException("No grown up found with id " + grownUpId));
    }

}