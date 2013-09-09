package no.gorman.please.child;

import no.gorman.please.RegisteredUser;
import no.gorman.please.common.Child;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static no.gorman.database.DatabaseColumns.child_id;
import static no.gorman.please.RegisteredUser.getCurrentUser;

public class ChildServlet extends HttpServlet {

    private final ChildCRUD dbActions;

    public ChildServlet(ChildCRUD dbActions) {
        this.dbActions = dbActions;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if  ("update".equals(action)) {
            update(request, response);
        }else if ("insert".equals(action)){
            insert(request, response);
        } else {
            response.getWriter().print("no action specified");
        }
    }

    private void insert(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RegisteredUser currentUser = getCurrentUser(request.getSession());
        String nickname = request.getParameter("nickname");
        String firstName = request.getParameter("child_first_name");
        String middleName = request.getParameter("child_middle_name");
        String lastName = request.getParameter("child_last_name");
        String color = request.getParameter("color");
        long dayCareId = Long.parseLong(request.getParameter("child_daycare_id"));
        if (currentUser.getGrownup().getDayCareId() != dayCareId) {
            System.err.println(currentUser.getGrownup().getEmail() + " cannot insert children into daycare " + dayCareId);
            return;
        }

        Child newChild = new Child();
        newChild.setDaycareId(dayCareId);
        newChild.setNickname(nickname);
        newChild.setFirstName(firstName);
        newChild.setLastName(lastName);
        newChild.setMiddleName(middleName);
        newChild.setColor(color);

        dbActions.newChild(newChild);
        response.sendRedirect("/editChild.html?ChildId=" + newChild.getChildId() + "&message=updated");
    }

    private void update(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RegisteredUser currentUser = getCurrentUser(request.getSession());
        final int childId = Integer.parseInt(request.getParameter("child_id"));
        Child updated = dbActions.findChild(currentUser.getGrownUpId(), childId);

        String nickname = request.getParameter("nickname");
        String firstName = request.getParameter("child_first_name");
        String middleName = request.getParameter("child_middle_name");
        String lastName = request.getParameter("child_last_name");
        String color = request.getParameter("color");

        updated.setNickname(nickname);
        updated.setFirstName(firstName);
        updated.setLastName(lastName);
        updated.setMiddleName(middleName);
        updated.setColor(color);

        dbActions.updateChild(updated);
        response.sendRedirect("/editChild.html?" + child_id.name() + "=" + childId + "&message=updated");
    }



}