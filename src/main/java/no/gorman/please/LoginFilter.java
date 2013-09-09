package no.gorman.please;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static no.gorman.please.LoginServlet.LOGGED_IN;

public class LoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (request.getServletContext().getContextPath().contains("login"))return;
        RegisteredUser currentUser = (RegisteredUser) ((HttpServletRequest)request).getSession().getAttribute(LOGGED_IN);
        if (currentUser == null) {
            throw new SecurityException("Not logged in");
        }
    }

    @Override
    public void destroy() {}
}
