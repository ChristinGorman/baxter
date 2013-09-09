package no.gorman.please.overview;

import com.google.gson.Gson;
import no.gorman.please.RegisteredUser;
import no.gorman.please.common.Child;
import no.gorman.please.common.Constants;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import static java.util.Arrays.asList;
import static no.gorman.database.DatabaseColumns.child_id;
import static no.gorman.database.DatabaseColumns.schedule_name;
import static no.gorman.please.RegisteredUser.getCurrentUser;

public class OverviewServlet extends HttpServlet {

    private OverviewCRUD dbActions;

    private final Map<String, BiFunction<HttpServletRequest, HttpServletResponse, Void>> actionMap = new ConcurrentHashMap<>();

    public OverviewServlet(OverviewCRUD dbActions) {
        this.dbActions = dbActions;
        actionMap.put("poll", this::poll);
        actionMap.put("scheduleUpdate", this::scheduleUpdate);
        actionMap.put("scheduleNames", this::getScheduleNames);
        actionMap.put("children", this::getChildren);
        actionMap.put("clubs", this::getClubs);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestedAction = request.getParameter("action");
        if (actionMap.containsKey(requestedAction)) {
            actionMap.get(requestedAction).apply(request, response);
        }else {
            throw new IllegalArgumentException(requestedAction + "? Don't understand what you mean");
        }
    }

    private Void poll(HttpServletRequest request, HttpServletResponse response) {
        RegisteredUser currentUser = getCurrentUser(request.getSession());
        Continuation c = ContinuationSupport.getContinuation(request);
        if (c.isInitial()) {
            c.setTimeout(10000);
            c.suspend(response);
            currentUser.waitForChanges(c);
        } else {
            print(response, new Gson().toJson(new ArrayList<Child>()));
        }
        return null;
    }

    private Void getChildren(HttpServletRequest request, HttpServletResponse response) {
        RegisteredUser currentUser = getCurrentUser(request.getSession());
        print(response, new Gson().toJson(dbActions.getOverviewOfChildren(currentUser.getGrownUpId())));
        return null;
    }

    private Void getClubs(HttpServletRequest request, HttpServletResponse response) {
        RegisteredUser currentUser = getCurrentUser(request.getSession());
        print(response, new Gson().toJson(dbActions.getClubsFor(currentUser.getGrownUpId())));
        return null;
    }

    private Void getScheduleNames(HttpServletRequest request, HttpServletResponse response) {
        RegisteredUser currentUser = getCurrentUser(request.getSession());
        print(response, new Gson().toJson(dbActions.getSchedulesFor(currentUser.getGrownUpId())));
        return null;
    }

    private Void scheduleUpdate(HttpServletRequest request, HttpServletResponse response) {
        Long childId = Long.valueOf(request.getParameter(child_id.name()));
        String scheduleName = request.getParameter(schedule_name.name());

        dbActions.updateSchedule(childId, scheduleName);
        print(response, new Gson().toJson(asList(dbActions.getChildWithSchedules(childId))));
        return null;
    }

    private void print(HttpServletResponse response, String json) {
        try {
            response.setContentType(Constants.CONTENT_TYPE);
            response.getWriter().print(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}