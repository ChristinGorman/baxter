package no.gorman.database;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Properties;

public class DBTestRunner extends BlockJUnit4ClassRunner {

    static {
        try {
            Properties props = new Properties();
            props.load(DB.class.getResourceAsStream("/baxter.properties"));
            DB.class.getResourceAsStream("/baxter.properties").close();
            DBFunctions.setupConnectionPool(props.getProperty("testdb"), props.getProperty("username"), props.getProperty("password"), 5);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not find properties file baxter.properties on classpath");
        }
    }

    public DBTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    public void runChild(FrameworkMethod method, RunNotifier notifier) {
        Connection conn = DBFunctions.getConnection();
        DB db = new DB(conn);
        try {
            conn.setAutoCommit(false);
            setDBField(db);
            super.runChild(method, notifier);
        }catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            db.rollback();
        }
    }

    private void setDBField(DB db) throws IllegalAccessException {
        for (Field f : getTestClass().getJavaClass().getDeclaredFields()) {
            if (f.getType().equals(DB.class)) {
                f.setAccessible(true);
                f.set(null, db);
                return;
            }
        }
    }
}
