package no.gorman.please.timeline;

import com.google.gson.Gson;
import no.gorman.please.RegisteredUser;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import static no.gorman.database.DatabaseColumns.*;
import static no.gorman.please.common.Constants.CONTENT_TYPE;
import static no.gorman.please.timeline.Event.withNameAndTime;

public class TimelineServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(TimelineServlet.class);
    private final TimelineCRUD actions;

    private final Map<String, BiFunction<HttpServletRequest, HttpServletResponse, Void>> actionMap = new ConcurrentHashMap<>();

    public TimelineServlet(TimelineCRUD actions) {
        this.actions = actions;
        actionMap.put("getEvents", this::getTimeline);
        actionMap.put("getImage", this::getImage);
        actionMap.put("getThumbnail", this::getThumbnail);
        actionMap.put("insert", this::newEvent);
        actionMap.put("getEvent", this::getEvent);
        actionMap.put("deleteEvent", this::deleteEvent);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!request.getParameterMap().containsKey("action")) {
            uploadFile(request, response);
        } else {
            String action = request.getParameter("action");
            log.debug(action);
            actionMap.get(action).apply(request, response);
        }
    }

    private Void uploadFile(HttpServletRequest request, HttpServletResponse response) {
        try {
            currentUser(request).addFiles(new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private Void newEvent(HttpServletRequest request, HttpServletResponse response) {
        String eventName = request.getParameter(event_name.name());
        Event newEvent = withNameAndTime(eventName, System.currentTimeMillis());
        newEvent.setEventCreator(currentUser(request).getGrownUpId());
        String[] children = request.getParameter(child_id.name()).split(",");
        String[] clubs = request.getParameter(club_id.name()).split(",");
        actions.insert(newEvent, children, clubs, currentUser(request).getAndRemoveUploadedFiles());
        print(response, new Gson().toJson(actions.populate(newEvent)));
        return null;
    }


    private Void getThumbnail(HttpServletRequest request, HttpServletResponse response)  {
        try {
            String idString = request.getParameter(attachment_id.name());
            //for some reason angular keeps requesting an image with id "{{attachment}}"
            // instead of actually replacing the brackets with a sensible value...
            if (StringUtils.isBlank(idString) || !StringUtils.isNumeric(idString)) return null;

            Long id = Long.parseLong(idString);
            byte[] file = actions.getThumbnail(id);
            response.setContentType("image/png");
            response.getOutputStream().write(file);
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Void getImage(HttpServletRequest request, HttpServletResponse response)  {
        try {
            String idString = request.getParameter(attachment_id.name());
            //for some reason angular keeps requesting an image with id "{{attachment}}"
            // instead of actually replacing the brackets with a sensible value...
            if (StringUtils.isBlank(idString) || !StringUtils.isNumeric(idString)) return null;

            Long id = Long.parseLong(idString);
            Attachment attachment = actions.getAttachment(id);
            byte[] file = attachment.getAttachment();
            response.setContentType(attachment.getContentType());
            response.getOutputStream().write(file);
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Void getTimeline(HttpServletRequest request, HttpServletResponse response) {
        print(response, new Gson().toJson(actions.getEvents(currentUser(request).getGrownUpId())));
        return null;
    }

    private Void getEvent(HttpServletRequest request, HttpServletResponse response) {
        print(response, new Gson().toJson(actions.getEvent(request.getParameter(event_id.name()))));
        return null;
    }

    public Void deleteEvent(HttpServletRequest request, HttpServletResponse response) {
        String eventId = request.getParameter(event_id.name());
        RegisteredUser user = currentUser(request);
        if (!Objects.equals(actions.getEventCreator(eventId), user.getGrownUpId())){
            throw new SecurityException(user.getGrownup().getFullName() + " does not have permission to delete event " + eventId);
        }
        actions.deleteEvent(eventId);
        print(response, "{}");
        return null;
    }

    private RegisteredUser currentUser(HttpServletRequest request) {
        return RegisteredUser.getCurrentUser(request.getSession());
    }

    private void print(HttpServletResponse response, String returnVal) {
        try {
            log.debug(returnVal);
            response.setContentType(CONTENT_TYPE);
            response.getWriter().print(returnVal);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

}
