package no.gorman.please;

import com.google.gson.Gson;
import no.gorman.database.*;
import no.gorman.please.common.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.lang.Integer.parseInt;
import static no.gorman.database.DatabaseColumns.*;
import static no.gorman.please.RegisteredUser.getCurrentUser;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.isNumeric;

public class DataServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RegisteredUser currentUser = getCurrentUser(request.getSession());
        if (currentUser == null) {
            throw new SecurityException("Not logged in");
        }

        String query = request.getParameter("query");
        response.setContentType(Constants.CONTENT_TYPE);

        DB db = new DB(DBFunctions.getConnection());
        Gson JSON = new Gson();
        switch (query) {
            case "getAllDaycareCenters":
                print(response, JSON.toJson(db.select(DayCareCenter.class, new OrderBy(daycare_name))));
                break;
            case "getGrownup":
                print(response, JSON.toJson(db.selectOnlyOne(GrownUp.class, new Where(grownup_id, " = ", parseInt(request.getParameter(grownup_id.name())))).get()));
                break;
            case "childrenInDaycare":
                print(response, JSON.toJson(db.select(Child.class, new OrderBy(nickname), new Where(child_daycare_id, " = ", parseInt(request.getParameter(daycare_id.name()))))));
                break;
            case "childrenForGrownup":
                String grownUpId = request.getParameter(grownup_id.name());
                Long id = (isNotBlank(grownUpId) && isNumeric(grownUpId)) ? parseInt(grownUpId) : currentUser.getGrownUpId();
                print(response, JSON.toJson(db.select(Child.class, new OrderBy(nickname), new Where(gc_grownup_id, " = ", id))));
                break;
            case "getChild":
                print(response, JSON.toJson(db.selectOnlyOne(Child.class, new Where(child_id, " = ", parseInt(request.getParameter(child_id.name())))).orElse(null)));
                break;
            case "getLoggedIn":
                GrownUp grownup = db.selectOnlyOne(GrownUp.class, new Where(grownup_id, "=", currentUser.getGrownUpId())).get();
                grownup.addClubs(db.select(DatabaseColumns.club_name, String.class, new Where(grg_grownup_id, "=", currentUser.getGrownUpId())));
                print(response, JSON.toJson(grownup));
                break;
            case "getAllGroups":
                print(response, JSON.toJson(db.select(Club.class, new OrderBy(club_name),
                        new Where(club_daycare_id, " = ", parseInt(request.getParameter(daycare_id.name()))))));
                break;
            case "getGroupsForGrownup":
                print(response, JSON.toJson(db.select(Club.class, new OrderBy(club_name), new Where(grg_grownup_id, " = ", parseInt(request.getParameter(grownup_id.name()))))));
            default:
                break;
        }
        db.rollback();
    }

    private void print(HttpServletResponse response, String value) throws IOException {
        response.getWriter().print(value);
    }
}