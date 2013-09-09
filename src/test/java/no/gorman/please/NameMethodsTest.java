package no.gorman.please;

import no.gorman.please.common.Child;
import no.gorman.please.common.GrownUp;
import org.fest.assertions.Assertions;
import org.junit.Test;

public class NameMethodsTest {

    @Test
    public void method_should_work_in_all_implementations() {
        Child me = new Child();
        me.setFirstName("Christin");
        me.setMiddleName("Rendalen");
        me.setLastName("Gorman");

        Assertions.assertThat(me.getFullName()).isEqualTo("Christin Rendalen Gorman");
        Assertions.assertThat(me.getInitials()).isEqualTo("CRG");

        GrownUp cal = new GrownUp();
        cal.setFirstName("Luke Skywalker");
        cal.setMiddleName(null);
        cal.setLastName("Gorman");
        Assertions.assertThat(cal.getFullName()).isEqualTo("Luke Skywalker Gorman");
        Assertions.assertThat(cal.getInitials()).isEqualTo("LSG");

        Child zoe = new Child();
        zoe.setFirstName("Zoe");
        zoe.setMiddleName("");
        Assertions.assertThat(zoe.getFullName()).isEqualTo("Zoe");
        Assertions.assertThat(zoe.getInitials()).isEqualTo("Z");
    }

}
