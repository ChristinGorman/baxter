package no.gorman.please;

import no.gorman.database.DBFunctions;
import no.gorman.please.child.ChildCRUD;
import no.gorman.please.child.ChildServlet;
import no.gorman.please.grownup.GrownUpCRUD;
import no.gorman.please.grownup.GrownUpServlet;
import no.gorman.please.overview.OverviewCRUD;
import no.gorman.please.overview.OverviewServlet;
import no.gorman.please.timeline.TimelineCRUD;
import no.gorman.please.timeline.TimelineServlet;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.DispatcherType;
import java.util.EnumSet;
import java.util.Properties;

import static no.gorman.please.utils.Transactional.transactional;

public class Main {

    public static void main(String[] args) throws Exception {

        Properties props = new Properties();
        props.load(Main.class.getResourceAsStream("/baxter.properties"));
        DBFunctions.setupConnectionPool(props.getProperty("db"), props.getProperty("username"), props.getProperty("password"), Integer.parseInt(props.getProperty("maxNumConnections")));

        Server server = new Server(Integer.valueOf(props.getProperty("PORT")));
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        ServletHolder overviewServlet = new ServletHolder(new OverviewServlet(transactional(new OverviewCRUD())));
        overviewServlet.setAsyncSupported(true);
        context.addServlet(overviewServlet, "/overview/*");
        
        ServletHolder loginServlet = new ServletHolder(new LoginServlet());
        context.addServlet(loginServlet, "/login/*");

        ServletHolder dataServlet = new ServletHolder(new DataServlet());
        context.addServlet(dataServlet, "/data/*");

        ServletHolder grownupServlet = new ServletHolder(new GrownUpServlet(transactional(new GrownUpCRUD())));
        context.addServlet(grownupServlet, "/grownup/*");

        ServletHolder childServlet = new ServletHolder(new ChildServlet(transactional(new ChildCRUD())));
        context.addServlet(childServlet, "/child/*");

        ServletHolder timelineServlet = new ServletHolder(new TimelineServlet(transactional(new TimelineCRUD())));
        context.addServlet(timelineServlet, "/timeline/*");

        context.setSecurityHandler(new ConstraintSecurityHandler());

        FilterHolder loginFilter = new FilterHolder(new LoginFilter());
        context.addFilter(loginFilter, "/", EnumSet.of(DispatcherType.REQUEST));

        DefaultHandler defaultHandler = new DefaultHandler();
        defaultHandler.setServeIcon(false);

        server.setSessionIdManager(new HashSessionIdManager());
        context.setSessionHandler(new SessionHandler());
        context.getSessionHandler().setSessionManager(new HashSessionManager());

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{"index.html"});
        resourceHandler.setResourceBase(".");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceHandler, context, defaultHandler });
        server.setHandler(handlers);

        server.start();
        server.join();

    }
}
