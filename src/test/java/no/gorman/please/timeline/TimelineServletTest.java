package no.gorman.please.timeline;

import com.google.gson.Gson;
import no.gorman.database.DB;
import no.gorman.database.DBTestRunner;
import no.gorman.database.Where;
import no.gorman.please.RegisteredUser;
import no.gorman.please.common.Child;
import no.gorman.please.common.DayCareCenter;
import no.gorman.please.common.GrownUp;
import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Arrays.asList;
import static no.gorman.database.DatabaseColumns.*;
import static no.gorman.please.LoginServlet.LOGGED_IN;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(DBTestRunner.class)
public class TimelineServletTest {

    Child one = new Child();
    Child two = new Child();
    Child three = new Child();
    Child four = new Child();

    GrownUp grownup = new GrownUp();

    DayCareCenter daycare = DayCareCenter.withName("test");

    Child five = new Child();
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpSession session = mock(HttpSession.class);
    PrintWriter writer = mock(PrintWriter.class);

    HashMap<String, String[]> map = new HashMap<String, String[]>() {{
        put("action", new String[]{"insert"});
        put(event_name.name(), new String[]{"walk in the woods"});
    }};

    public static DB db;

    @Before
    public void setup() throws Exception {
        db.insert(daycare);
        asList(one, two, three, four, five).forEach(child -> child.setDaycareId(daycare.getDayCareCenterId()));

        db.insert(one, two, three, four, five);

        grownup.setDayCareId(daycare.getDayCareCenterId());
        db.insert(grownup);

        db.link(grownup, one);
        db.link(grownup, two);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(LOGGED_IN)).thenReturn(new RegisteredUser(grownup));
        when(request.getParameterMap()).thenReturn(map);
    }

    @Test
    public void should_create_event_and_link_to_children() throws Exception {

        TimelineCRUD actions = new TimelineCRUD(db);
        TimelineServlet servlet = new TimelineServlet(actions);

        when(request.getParameter(eq("action"))).thenReturn("insert");
        when(request.getParameter(eq(event_name.name()))).thenReturn("walk in the woods");
        when(request.getParameter(eq(event_time.name()))).thenReturn(String.valueOf(System.currentTimeMillis()));
        when(request.getParameter(eq(child_id.name()))).thenReturn(one.getChildId() + "," + two.getChildId() + "," + three.getChildId());
        when(request.getParameter(eq(club_id.name()))).thenReturn("");
        when(response.getWriter()).thenReturn(writer);

        servlet.doPost(request, response);
        Assertions.assertThat(db.selectOnlyOne(Event.class).get().getEventName()).isEqualTo("walk in the woods");
        Assertions.assertThat(db.select(ec_child_id, Long.TYPE)).containsOnly(one.getChildId(), two.getChildId(), three.getChildId());
    }

    @Test
    public void should_show_correct_events() throws Exception {
        Event e = new Event();
        e.setEventName("test");
        e.setEventTime(System.currentTimeMillis());

        db.insert(e);
        asList(one, two, three).forEach(child -> db.link(child, e));

        final AtomicReference<String> responseWritten = new AtomicReference<>();
        TimelineCRUD actions = new TimelineCRUD(db);
        TimelineServlet servlet = new TimelineServlet(actions);
        when(request.getParameter(eq("action"))).thenReturn("getEvents");
        when(response.getWriter()).thenReturn(new PrintWriter(mock(Writer.class)) {
            @Override
            public void print(String s) {
                responseWritten.set(s);
            }
        });

        servlet.doPost(request, response);

        e.addChildren(db.select(ChildName.class, new Where(ec_event_id, "=", e.getEventId())));
        Assertions.assertThat(responseWritten.get()).isEqualTo(new Gson().toJson(asList(e)));
    }

}
