package no.gorman.please.utils;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import no.gorman.please.utils.WithDatabase;
import no.gorman.database.DB;
import no.gorman.database.DBFunctions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Transactional {

    @SuppressWarnings("unchecked")
    public static <T extends WithDatabase> T transactional(final T dbActions) {

        ProxyFactory pf = new ProxyFactory();
        pf.setSuperclass(dbActions.getClass());
        try {
            return (T) pf.create(null, null, new MethodHandler() {

                @Override
                public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
                    DB db = new DB(DBFunctions.getConnection());
                    dbActions.setDB(db);
                    try {
                        Object returnVal = thisMethod.invoke(dbActions, args);
                        db.commitAndReleaseConnection();
                        return returnVal;
                    } catch (InvocationTargetException e) {
                        db.rollback();
                        throw e.getTargetException();
                    } catch (Exception e) {
                        db.rollback();
                        throw e;
                    }
                }
            });
        } catch (NoSuchMethodException | IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
