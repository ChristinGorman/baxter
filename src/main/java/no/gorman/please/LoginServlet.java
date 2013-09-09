package no.gorman.please;

import no.gorman.database.*;
import no.gorman.please.common.ChildPK;
import no.gorman.please.common.GrownUp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static no.gorman.database.BigBrother.spyOn;
import static no.gorman.database.DatabaseColumns.*;

public class LoginServlet extends HttpServlet {

    public static final String LOGGED_IN = "logged_in";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("email");
        String password = request.getParameter("password");

        DB db = new DB(DBFunctions.getConnection());
        Optional<GrownUp> grownUp = db.selectOnlyOne(GrownUp.class, new Where(DatabaseColumns.password, " = ", password), new Where(email, " = ", username));
        if (grownUp.isPresent()) {
            RegisteredUser registeredUser = new RegisteredUser(grownUp.get());
            grownUp.get().addClubs(db.select(club_name, String.class, new Where(grg_grownup_id, " = ", grownUp.get().getGrownUpId())));
            List<ChildPK> myChildren = db.select(ChildPK.class, new Where(gc_grownup_id, " = ", grownUp.get().getGrownUpId()));
            myChildren.forEach(child -> spyOn(child, registeredUser));
            RegisteredUser.login(request.getSession(), registeredUser);
            response.sendRedirect("/childOverview.html");
        } else {
            System.err.println("Login failed " + email + ", " + password);
        }
        db.commitAndReleaseConnection();
    }
}