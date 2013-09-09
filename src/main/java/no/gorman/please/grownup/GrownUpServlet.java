package no.gorman.please.grownup;

import no.gorman.database.DatabaseColumns;
import no.gorman.please.common.GrownUp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

import static no.gorman.database.DatabaseColumns.*;

public class GrownUpServlet extends HttpServlet {

    private final GrownUpCRUD dbActions;

    public GrownUpServlet(GrownUpCRUD dbActions) {
        this.dbActions = dbActions;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action) {
            case "insert" : insert(request, response); break;
            case "update" : update(request, response); break;
            default: throw new IllegalArgumentException("no valid action supplied");
        }
    }

    private void insert(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long daycare = Integer.parseInt(request.getParameter("grownup_daycare_id"));
        String email = request.getParameter(DatabaseColumns.email.name());
        String password = request.getParameter(DatabaseColumns.password.name());
        String firstName = request.getParameter(grownup_first_name.name());
        String middleName = request.getParameter(grownup_middle_name.name());
        String lastName = request.getParameter(grownup_last_name.name());
        String[] clubIds = request.getParameterValues(club_id.name());
        String[] childIds = request.getParameterValues(child_id.name());

        GrownUp newOne = new GrownUp();
        newOne.setEmail(email);
        newOne.setPassword(password);
        newOne.setDayCareId(daycare);
        newOne.setLastName(lastName);
        newOne.setMiddleName(middleName);
        newOne.setFirstName(firstName);

        dbActions.newGrownup(newOne, childIds, clubIds);
        response.sendRedirect("/index.html?message=" + URLEncoder.encode("new user added", "UTF-8" ));
    }

    private void update(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int grownUpId = Integer.parseInt(request.getParameter("grownup_id"));
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String firstName = request.getParameter(grownup_first_name.name());
        String middleName = request.getParameter(grownup_middle_name.name());
        String lastName = request.getParameter(grownup_last_name.name());
        String version = request.getParameter(grownup_version.name());
        String[] clubIds = request.getParameterValues(club_id.name());
        String[] childIds = request.getParameterValues(child_id.name());

        GrownUp updated = dbActions.getGrownup(grownUpId);
        updated.setEmail(email);
        updated.setPassword(password);
        updated.setLastName(lastName);
        updated.setMiddleName(middleName);
        updated.setFirstName(firstName);
        updated.setVersion(Integer.parseInt(version));

        dbActions.updateGrownup(updated, childIds, clubIds);

        response.sendRedirect("/editGrownup.html?" + grownup_id.name() + "=" + grownUpId);
    }



}