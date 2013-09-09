package no.gorman.please.utils;

import no.gorman.database.DB;

import java.util.ArrayList;
import java.util.List;

public abstract class WithDatabase {

    private final ThreadLocal<DB> db;

    public WithDatabase() {
        this(null);
    }

    public WithDatabase(DB db) {
        this.db = new ThreadLocal<>();
        this.db.set(db);
    }

    public void setDB(DB db) {
        this.db.set(db);
    }

    public DB getDB() {
        return db.get();
    }

}
