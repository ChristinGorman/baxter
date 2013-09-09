package no.gorman.please;

import com.google.gson.Gson;
import no.gorman.database.BigBrother;
import no.gorman.please.common.GrownUp;
import org.apache.commons.fileupload.FileItem;
import org.eclipse.jetty.continuation.Continuation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Collections.synchronizedCollection;
import static no.gorman.please.LoginServlet.LOGGED_IN;

public class RegisteredUser implements BigBrother.Spy{
    private static final Logger log = LoggerFactory.getLogger(RegisteredUser.class);
    private GrownUp user;
    private final AtomicReference<Continuation> continuation;
    private final AtomicLong timestamp;
    private Collection<FileItem> uploadedFiles = synchronizedCollection(new ArrayList<>());

    public static RegisteredUser getCurrentUser(HttpSession session) {
        return (RegisteredUser) session.getAttribute(LOGGED_IN);
    }

    public static void login(HttpSession session, RegisteredUser user) {
        session.setAttribute(LOGGED_IN, user);
    }

    public RegisteredUser(GrownUp grownup) {
        this.user = grownup;
        this.timestamp = new AtomicLong(0);
        this.continuation = new AtomicReference<>();
    }

    public void resume(Object suspect) {
        log.debug("resuming continuation because of updates to " + suspect);
        Continuation c = continuation.getAndSet(null);
        if (c == null || c.isExpired()) {
            return;
        }
        ServletResponse response = c.getServletResponse();
        response.setContentType("text/json;charset=utf-8");
        print(response, new Gson().toJson(Arrays.asList(suspect)));
        c.complete();
    }

    private void print(ServletResponse response, String json) {
        try {
            log.debug(json);
            response.getWriter().print(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public GrownUp getGrownup() {
        return user;
    }

    public long getGrownUpId() {
        return user.getGrownUpId();
    }

    public void waitForChanges(Continuation c) {
        continuation.set(c);
    }

    @Override
    public void suspectAltered(Object suspect) {
        resume(suspect);
    }

    public void addFiles(Collection<FileItem> uploadedFiles) {
        this.uploadedFiles.addAll(uploadedFiles);
    }

    public Collection<FileItem> getAndRemoveUploadedFiles() {
        Collection<FileItem> tmp = new ArrayList<>(this.uploadedFiles);
        uploadedFiles.clear();
        return tmp;
    }
}
