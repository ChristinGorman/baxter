package no.gorman.database;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static no.gorman.database.ColumnType.*;
import static no.gorman.database.Table.*;

public enum DatabaseColumns {

    UNDEFINED(null, null),

    daycare_id(Long.TYPE, Table.daycare, PrimaryKey),
    daycare_name(String.class, Table.daycare),
    
    child_id(Long.TYPE, Table.child, PrimaryKey),
	dob(LocalDate.class, Table.child),
	child_first_name(String.class, Table.child),
	child_middle_name(String.class, Table.child),
	child_last_name(String.class, Table.child),
	nickname(String.class, Table.child),
	color(String.class, Table.child),
	child_daycare_id(Long.TYPE, Table.child, ForeignKey, Table.daycare),
	child_version(Integer.TYPE, Table.child, Version),

	gc_grownup_id(Long.TYPE, grownup_child, ForeignKey, Table.grownup),
	gc_child_id(Long.TYPE, grownup_child, ForeignKey, Table.child),

	grownup_id(Long.TYPE, grownup, PrimaryKey),
	grownup_first_name(String.class, Table.grownup),
    grownup_middle_name(String.class, Table.grownup),
    grownup_last_name(String.class, Table.grownup),
	telephone(String.class, Table.grownup),
	email(String.class, Table.grownup, 255),
	password(String.class, Table.grownup),
    grownup_version(Integer.TYPE, Table.grownup, Version),
    grownup_daycare_id(Long.TYPE, Table.grownup, ForeignKey, Table.daycare),

    event_id(Long.TYPE, Table.event, PrimaryKey),
    event_name(String.class, Table.event, 500),
    event_time(Long.TYPE, Table.event),

    ec_child_id(Long.TYPE, Table.event_child, ForeignKey, child),
    ec_event_id(Long.TYPE, Table.event_child, ForeignKey, Table.event),

    attachment_id(Long.TYPE, Table.attachment, PrimaryKey),
    attachment(byte[].class, Table.attachment),
    attachment_event_id(Long.TYPE, Table.attachment, ForeignKey, Table.event),
    content_type(String.class, Table.attachment, 50),

    schedule_id(Long.TYPE, Table.schedule, PrimaryKey),
    schedule_child_id(Long.TYPE, Table.schedule, ForeignKey, Table.child),
    last_event(Long.TYPE, Table.schedule),
    interval(Integer.TYPE,  Table.schedule),
    schedule_name(String.class, Table.schedule),

    club_id(Long.TYPE, Table.club, PrimaryKey),
    club_name(String.class, Table.club),
    club_color(String.class, Table.club),
    club_daycare_id(Long.TYPE, Table.club, ForeignKey, daycare),

    grc_child_id(Long.TYPE, Table.club_child, ForeignKey, child),
    grc_club_id(Long.TYPE, Table.club_child, ForeignKey, club),

    grg_grownup_id(Long.TYPE, Table.club_grownup, ForeignKey, grownup),
    grg_club_id(Long.TYPE, Table.club_grownup, ForeignKey, club),

    ecl_club_id(Long.TYPE, Table.event_club, ForeignKey, club),
    ecl_event_id(Long.TYPE, Table.event_club, ForeignKey, Table.event);
	
    private final Table table;
    private final Table joinedTo;
    private final ColumnType type;
    private final Class<?> clazz;
    private final int length;

    private DatabaseColumns(Class<?> clazz, Table table) {
        this(clazz, table, 50);
    }

    private DatabaseColumns(Class<?> clazz, Table table, int length) {
        this.clazz = clazz;
        this.table = table;
        type = ColumnType.Field;
        this.joinedTo = null;
        this.length = length;
    }

    private DatabaseColumns(Class<?> clazz, Table table, ColumnType type) {
        this.clazz = clazz;
        this.table = table;
        this.type = type;
        this.joinedTo = null;
        this.length = 50;
    }
    
    private DatabaseColumns(Class<?> clazz, Table table, ColumnType type, Table joinedTo) {
        this.clazz = clazz;
        this.table = table;
        this.type = type;
        this.joinedTo = joinedTo;
        this.length = 50;
    }

    public Table getTable() {
        return this.table;
    }

    public Class<?> getFieldClass() {
        return clazz;
    }

    public int getDataLength() {
        return length;
    }

    public Table getJoinedTo() {
        return joinedTo;
    }

    public ColumnType getType() {
        return type;
    }

    public static Optional<DatabaseColumns> getForeignKey(Table primaryKeyTable, Table foreignKeyTable) {
        return asList(values())
                .stream()
                .filter(col -> col.getJoinedTo() == primaryKeyTable && col.getTable() == foreignKeyTable)
                .findFirst();
    }

    public static Optional<DatabaseColumns> getPrimaryKey(Table table) {
        return asList(values())
                .stream()
                .filter(c -> (c.getType() == PrimaryKey && c.getTable() == table))
                .findFirst();
    }

    public static Collection<DatabaseColumns> incomingReferenceColumns(Table t) {
        return asList(DatabaseColumns.values()).stream()
                .filter(col -> col.getType() == ForeignKey && col.getJoinedTo() == t)
                .<List<DatabaseColumns>>collect(toList());
    }

    public static Collection<DatabaseColumns> getColumnsFor(Table t) {
        return asList(DatabaseColumns.values()).stream().filter(c -> c.getTable() == t).<List<DatabaseColumns>>collect(toList());
    }
}