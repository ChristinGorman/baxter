package no.gorman.please.overview;

import no.gorman.database.DB;
import no.gorman.database.OrderBy;
import no.gorman.database.Where;
import no.gorman.please.common.Club;
import no.gorman.please.utils.WithDatabase;

import java.util.List;

import static no.gorman.database.DatabaseColumns.*;

public class OverviewCRUD extends WithDatabase{

    
    public void updateSchedule(Long childId, String scheduleName) {
        Schedule schedule = getDB().selectOnlyOne(Schedule.class,
                new Where(schedule_child_id, " = ", childId),
                new Where(schedule_name, " Like ", scheduleName))
                .orElseThrow(()->new IllegalArgumentException("Child " + childId + " has no schedule named " + scheduleName));
        schedule.setLastEventTime(System.currentTimeMillis());
        getDB().update(schedule);
    }


    public OverviewOfChild getChildWithSchedules(Long childId) {
        OverviewOfChild child = getDB()
                .selectOnlyOne(OverviewOfChild.class, new Where(child_id, " = ", childId))
                .orElseThrow(() -> new IllegalArgumentException("child " + childId + " not found"));
        child.addSchedules(getDB().select(Schedule.class, new Where(schedule_child_id, " = ", childId)));
        return child;
    }


    public List<OverviewOfChild> getOverviewOfChildren(long grownupId) {
        DB db = getDB();
        List<OverviewOfChild> children = db.select(OverviewOfChild.class, new OrderBy(nickname), new Where(gc_grownup_id, " = ", grownupId));
        children.forEach(child -> child.addSchedules(db.select(Schedule.class, new Where(schedule_child_id, " = ", child.getChildId()))));
        children.forEach(child -> child.addGroups(db.select(club_name, String.class, new Where(grc_child_id, " = ", child.getChildId()))));
        return children;
    }

    public List<String> getSchedulesFor(long grownupId) {
        return getDB().select(schedule_name, String.class, new Where(gc_grownup_id, " = ", grownupId));
    }

    public List<Club> getClubsFor(long grownUpId) {
        return getDB().select(Club.class, new Where(grg_grownup_id, " = ", grownUpId));
    }
}