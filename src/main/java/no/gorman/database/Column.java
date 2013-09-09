package no.gorman.database;

import java.lang.annotation.*;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    DatabaseColumns column() default DatabaseColumns.UNDEFINED;
    Function function() default Function.NONE;
}
