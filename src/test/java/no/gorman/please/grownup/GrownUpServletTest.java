package no.gorman.please.grownup;

import no.gorman.database.DB;
import no.gorman.database.DBTestRunner;
import no.gorman.database.Where;
import no.gorman.please.RegisteredUser;
import no.gorman.please.common.Club;
import no.gorman.please.common.GrownUp;
import no.gorman.please.common.Child;
import no.gorman.please.common.DayCareCenter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static no.gorman.database.DatabaseColumns.*;
import static no.gorman.please.LoginServlet.LOGGED_IN;
import static org.fest.assertions.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(DBTestRunner.class)
public class GrownUpServletTest {

    private GrownUpServlet servlet;
    private DayCareCenter torsbergskogen = DayCareCenter.withName("Torsbergskogen");
    private Child baxter = Child.withNickname("Baxter");
    private Child zoe = Child.withNickname("Zoe");
    private Child dollyo = Child.withNickname("Bob");
    private Club geeks = Club.withName("Geeks");
    private Club nerds = Club.withName("Nerds");

    public static DB db;

    @Before
    public void setup(){
        db.insert(torsbergskogen, baxter, zoe, dollyo, geeks, nerds);
        GrownUpCRUD dbActions = new GrownUpCRUD(){};
        dbActions.setDB(db);
        servlet = new GrownUpServlet(dbActions);

    }

    @Test
    public void should_insert_new_grownup() throws Exception {
        servlet.doPost(newGrownUp(), mock(HttpServletResponse.class));

        GrownUp grownUp = db.selectOnlyOne(GrownUp.class, new Where(email, "Like", "%email@email.com%")).orElseThrow(() -> new Exception());
        assertThat(grownUp.getPassword()).isEqualTo("very secret");

        List<Child> children = db.select(Child.class, new Where(gc_grownup_id, " = ", grownUp.getGrownUpId()));
        assertThat(children).containsOnly(baxter, zoe, dollyo);

        List<Club> groups = db.select(Club.class, new Where(grg_grownup_id, " = ", grownUp.getGrownUpId()));
        assertThat(groups).containsOnly(geeks, nerds);
    }

    @Test
    public void should_update_grownup() throws Exception {
        servlet.doPost(newGrownUp(), mock(HttpServletResponse.class));
        servlet.doPost(updateGrownup(), mock(HttpServletResponse.class));

        GrownUp grownUp = db.selectOnlyOne(GrownUp.class, new Where(email, "Like", "%changed@email.com%")).orElseThrow(() -> new Exception());
        assertThat(grownUp.getPassword()).isEqualTo("not so secret");

        List<Child> children = db.select(Child.class, new Where(gc_grownup_id, " = ", grownUp.getGrownUpId()));
        assertThat(children).containsOnly(zoe, dollyo);

        List<Club> groups = db.select(Club.class, new Where(grg_grownup_id, " = ", grownUp.getGrownUpId()));
        assertThat(groups).containsOnly(geeks);

    }

    private HttpServletRequest newGrownUp() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter(argThat(equalTo("action")))).thenReturn("insert");
        when(request.getParameter(argThat(equalTo(email.name())))).thenReturn("email@email.com");
        when(request.getParameter(argThat(equalTo(password.name())))).thenReturn("very secret");

        List<String> childIds = db.select(child_id, Long.TYPE).stream().map(numericId -> String.valueOf(numericId)).collect(toList());
        when(request.getParameterValues(argThat(equalTo(child_id.name())))).thenReturn(childIds.toArray(new String[0]));

        List<String> clubIds = db.select(club_id, Long.TYPE).stream().map(numericId -> String.valueOf(numericId)).collect(toList());
        when(request.getParameterValues(argThat(equalTo(club_id.name())))).thenReturn(clubIds.toArray(new String[0]));

        when(request.getParameter(argThat(equalTo(grownup_daycare_id.name())))).thenReturn(String.valueOf(torsbergskogen.getDayCareCenterId()));
        addSession(request);
        return request;
    }

    private void addSession(HttpServletRequest request) {
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(Mockito.eq(LOGGED_IN))).thenReturn(new RegisteredUser(new GrownUp()));
    }

    private HttpServletRequest updateGrownup() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter(argThat(equalTo("action")))).thenReturn("update");
        when(request.getParameter(argThat(equalTo(email.name())))).thenReturn("changed@email.com");
        when(request.getParameter(argThat(equalTo(password.name())))).thenReturn("not so secret");
        when(request.getParameter(argThat(equalTo(grownup_version.name())))).thenReturn("0");
        when(request.getParameter(argThat(equalTo(grownup_id.name())))).thenReturn(String.valueOf(db.selectOnlyOne(GrownUp.class).get().getGrownUpId()));

        List<String> childIds = db.select(child_id, Long.TYPE, new Where(child_id, " != ", baxter.getChildId()))
                .stream()
                .map(intId -> String.valueOf(intId))
                .collect(toList());
        when(request.getParameterValues(argThat(equalTo(child_id.name())))).thenReturn(childIds.toArray(new String[0]));

        List<String> clubIds = db.select(club_id, Long.TYPE, new Where(club_id, " != ", nerds.getClubId()))
                .stream()
                .map(intId -> String.valueOf(intId))
                .collect(toList());
        when(request.getParameterValues(argThat(equalTo(club_id.name())))).thenReturn(clubIds.toArray(new String[0]));
        addSession(request);
        return request;
    }
}
