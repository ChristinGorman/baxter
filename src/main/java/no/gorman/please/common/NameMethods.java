package no.gorman.please.common;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;
import static org.apache.commons.lang.StringUtils.trim;

public interface NameMethods {

    String getNickname();

    String getFirstName();

    String getMiddleName();

    String getLastName();

    default String getFullName() {
        return trim(join(asList(getFirstName(), getMiddleName(), getLastName()), " ").replace("  ", " "));
    }

    default String getInitials() {
        return asList(getFullName().split(" ")).stream()
                .map(name -> String.valueOf(name.charAt(0)))
                .reduce((initialA, initialB) -> initialA + initialB).get();
    }
}