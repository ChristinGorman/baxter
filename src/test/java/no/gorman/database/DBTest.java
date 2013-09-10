package no.gorman.database;

import no.gorman.please.common.Child;
import no.gorman.please.common.DayCareCenter;
import no.gorman.please.common.GrownUp;
import no.gorman.please.overview.OverviewOfChild;
import no.gorman.please.overview.Schedule;
import no.gorman.please.timeline.Event;
import org.fest.assertions.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Arrays.asList;
import static no.gorman.database.DBFunctions.findJoins;
import static no.gorman.database.DatabaseColumns.*;
import static no.gorman.database.Function.COUNT;
import static org.fest.assertions.Assertions.assertThat;

@RunWith(DBTestRunner.class)
public class DBTest {

    DayCareCenter torsbergskogen = new DayCareCenter("Torsbergskogen");
    DayCareCenter blåbærtoppen = new DayCareCenter("Blåbærtoppen");

    ChildTableEntry baxter = new ChildTableEntry();
    ChildTableEntry zoe = new ChildTableEntry();
    ChildTableEntry dollyo = new ChildTableEntry();

    GrownUp me = new GrownUp();
    GrownUp you = new GrownUp();

    public static DB db;

    @Before
    public void setup() {
        db.insert(torsbergskogen);
        db.insert(blåbærtoppen);

        baxter.Nickname = "Baxter";
        baxter.DOB = LocalDate.of(2009, 5, 5);
        baxter.childDayCareCenter = torsbergskogen.getDayCareCenterId();
        db.insert(baxter);

        zoe.Nickname = "Zoé";
        zoe.DOB = LocalDate.of(2011, 10, 3);
        zoe.childDayCareCenter = torsbergskogen.getDayCareCenterId();
        db.insert(zoe);

        dollyo.Nickname = "Dollyo";
        dollyo.DOB = LocalDate.of(2002, 3, 8);
        dollyo.childDayCareCenter = blåbærtoppen.getDayCareCenterId();
        db.insert(dollyo);

        me.setEmail("me@somewhere.com");
        me.setDayCareId(torsbergskogen.getDayCareCenterId());
        db.insert(me);

        you.setEmail("you@somewhere.com");
        you.setDayCareId(blåbærtoppen.getDayCareCenterId());
        db.insert(you);
    }

    @Test
    public void shouldRunSQL() {
        List<ChildTableEntry> children = db.runSQL(ChildTableEntry.class, "SELECT * FROM child", new ArrayList<>());
        Assertions.assertThat(children).containsOnly(baxter, zoe, dollyo);
    }

    @Test
    public void should_create_simple_query() {
        List<ChildTableEntry> children = db.select(ChildTableEntry.class);
        Assertions.assertThat(children).containsOnly(baxter, zoe, dollyo);
    }

    @Test
    public void should_select_entity_with_fields_from_multiple_tables() {
        GrownUp me = db.selectOnlyOne(GrownUp.class, new Where(DatabaseColumns.email, " LIKE ", "%me@%")).get();
        assertThat(me.getEmail()).isEqualTo("me@somewhere.com");
        assertThat(me.getDayCareName()).isEqualTo(torsbergskogen.getDayCareName());
    }

    @Test
    public void should_update_child() throws Exception {
        Child baxter = db.selectOnlyOne(Child.class, new Where(nickname, " like ", "%Baxter%")).get();
        String newNickname = "Fartbox";
        baxter.setNickname(newNickname);
        int version = baxter.getVersion();
        db.update(baxter);
        baxter = db.selectOnlyOne(Child.class, new Where(child_id, " = ", baxter.getChildId())).get();
        Assert.assertEquals(version + 1, baxter.getVersion());
        Assert.assertEquals(newNickname, baxter.getNickname());
    }
    
    @Test
    public void should_create_many_to_many_table_entry() throws Exception {
        db.link(baxter, me);
        Assert.assertEquals(baxter, db.selectOnlyOne(ChildTableEntry.class, new Where(gc_grownup_id, " = ", me.getGrownUpId())).get());
    }

    @Test
    public void should_remove_many_to_many_table_entry() throws Exception {
        db.link(baxter, me);
        Assert.assertEquals(baxter, db.selectOnlyOne(ChildTableEntry.class, new Where(gc_grownup_id, " = ", me.getGrownUpId())).get());
        db.unlink(baxter, me);
        assertThat(db.select(ChildTableEntry.class, new Where(DatabaseColumns.gc_grownup_id, " = ", me.getGrownUpId()))).isEmpty();
    }
    
    @Test
    public void should_group_by() throws Exception {
        List<DayCareChildCount> count = db.select(DayCareChildCount.class);
        assertThat(count).containsOnly(new DayCareChildCount(torsbergskogen, 2), new DayCareChildCount(blåbærtoppen, 1));
    }
    
    @Test
    public void should_notify_listeners_of_changes_made() throws Exception {
        AtomicReference<CountDownLatch> countDown = new AtomicReference<>();
        countDown.set(new CountDownLatch(1));
        BigBrother.spyOn(baxter, suspect -> countDown.get().countDown());

        baxter.Nickname = "silly billy";
        db.update(baxter);
        db.onSuccessActions.forEach(r -> r.run());
        assertThat(countDown.get().await(50, TimeUnit.MILLISECONDS)).isTrue();

        countDown.set(new CountDownLatch(1));
        Schedule schedule = new Schedule();
        schedule.setName("test");
        schedule.setChildId(baxter.childId);
        db.insert(schedule);
        db.onSuccessActions.forEach(r -> r.run());
        assertThat(countDown.get().await(50, TimeUnit.MILLISECONDS)).isTrue();

        countDown.set(new CountDownLatch(1));
        schedule.setInterval(10);
        db.update(schedule);
        db.onSuccessActions.forEach(r -> r.run());
        assertThat(countDown.get().await(50, TimeUnit.MILLISECONDS)).isTrue();
    }
    
    @Test(expected=ConcurrentModificationException.class)
    public void should_not_allow_concurrent_updates() throws Exception {
        int originalVersion = baxter.ChildVersion;
        baxter.Nickname = "SillyBilly";

        OverviewOfChild baxter1 = db.selectOnlyOne(OverviewOfChild.class, new Where(child_id, " = ", baxter.childId)).get();
        baxter1.setColor("#ff0000");

        db.update(baxter);
        Assert.assertEquals(originalVersion+1, baxter.ChildVersion);

        db.update(baxter1);
    }

    @Test
    public void should_order_correctly() {

        List<ChildTableEntry> byNickname = db.select(ChildTableEntry.class, new OrderBy(nickname, OrderBy.ASCENDING));
        Assertions.assertThat(byNickname).containsExactly(baxter, dollyo, zoe);

        List<ChildTableEntry> reverseOrder = db.select(ChildTableEntry.class, new OrderBy(nickname, OrderBy.DESCENDING));
        Assertions.assertThat(reverseOrder).containsExactly(zoe, dollyo, baxter);
    }

    @Test
    public void should_order_correctly_for_group_by_statements() {
        db.insert(newEvent("one"), newEvent("two"), newEvent("three"));
        db.select(Event.class).forEach(event -> db.link(baxter, event));
        db.select(Event.class, new Where(event_name, " IN('one', 'two')")).forEach(event -> db.link(zoe, event));
        db.select(Event.class, new Where(event_name, " LIKE ", "three")).forEach(event -> db.link(dollyo, event));
        List<EventCount> count = db.select(EventCount.class, new OrderBy(nickname));
        assertThat(count).containsExactly(new EventCount(baxter.childId, 3), new EventCount(dollyo.childId, 1), new EventCount(zoe.childId, 2));
    }

    @Test
    public void should_find_one_to_many_link() {
        Collection<Join> joins = findJoins(asList(Table.daycare, Table.grownup));
        assertThat(joins).containsOnly(new Join(DatabaseColumns.daycare_id, DatabaseColumns.grownup_daycare_id));
    }

    @Test
    public void should_find_many_to_many_link() {
        List<Join> joins = new ArrayList<>(findJoins(asList(Table.child, Table.grownup)));
        assertThat(joins).containsOnly(
                new Join(child_id, gc_child_id),
                new Join(grownup_id, gc_grownup_id));
    }

    @Test
    public void find_joins_between_all_tables() {
        Collection<Join> joins = findJoins(asList(Table.attachment, Table.child, Table.event));
        Assertions.assertThat(joins).containsOnly(
                new Join(event_id, ec_event_id),
                new Join(child_id, ec_child_id),
                new Join(event_id, attachment_event_id));
    }

    private Event newEvent(String name) {
        Event event  = new Event();
        event.setEventName(name);
        event.setEventTime(System.currentTimeMillis());
        return event;
    }

    public static class EventCount {
        @Column(column=child_id)
        private Long childId;

        @Column(column=ec_event_id, function= COUNT)
        private long numEvents;

        public EventCount(){
        }

        public EventCount(long childId, long count) {
            this.childId = childId;
            this.numEvents = count;
        }

        @Override
        public boolean equals(Object o) {
            return Objects.equals(childId, ((EventCount)o).childId) && Objects.equals(numEvents, ((EventCount)o).numEvents);
        }

        @Override
        public int hashCode() {
            return Objects.hash(childId, numEvents);
        }

        @Override
        public String toString() {
            return childId + " " + numEvents;
        }
    }

    public static class DayCareChildCount {
        @Column(column=daycare_id)
        private Long DayCareCenterId;

        @Column(column=daycare_name)
        private String DayCareName;
        
        @Column(column=child_id, function= COUNT)
        private long numChildren;

        public DayCareChildCount(){

        }

        public DayCareChildCount(DayCareCenter daycare, int count) {
            this.DayCareCenterId = daycare.getDayCareCenterId();
            this.DayCareName = daycare.getDayCareName();
            this.numChildren = count;
        }

        @Override
        public int hashCode() {
            return Objects.hash(DayCareCenterId, numChildren);
        }

        @Override
        public boolean equals(Object obj) {
            return Objects.equals(DayCareCenterId, ((DayCareChildCount)obj).DayCareCenterId) && numChildren == ((DayCareChildCount)obj).numChildren;
        }

        @Override
        public String toString() {
            return DayCareName + ": " + numChildren;
        }
    }
    
    public static class ChildTableEntry {
        @Column(column=DatabaseColumns.child_id)
        private Long childId;

        @Column(column=DatabaseColumns.dob)
        private LocalDate DOB;

        @Column(column=DatabaseColumns.nickname)
        private String Nickname;

        @Column(column=DatabaseColumns.child_version)
        private int ChildVersion;

        @Column(column=DatabaseColumns.child_daycare_id)
        private Long childDayCareCenter;

        @Override
        public String toString() {
            return childId + " " + Nickname;
        }


        @Override
        public int hashCode() {
            return Objects.hash(childId);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ChildTableEntry other = (ChildTableEntry) obj;
            return Objects.equals(childId, other.childId);
        }
    }
}