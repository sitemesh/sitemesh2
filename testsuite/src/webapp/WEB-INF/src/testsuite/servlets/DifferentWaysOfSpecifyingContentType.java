package testsuite.servlets;

import com.opensymphony.module.sitemesh.RequestConstants;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.IOException;

public class DifferentWaysOfSpecifyingContentType extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ("yes".equals(request.getParameter("kill"))) {
            request.setAttribute(RequestConstants.DISABLE_BUFFER_AND_DECORATION, Boolean.TRUE);
        }

        String approach = request.getParameter("approach");

        if ("setContentType".equals(approach)) {
            response.setContentType("text/html");
        } else if ("addHeader".equals(approach)) {
            response.addHeader("Content-type", "text/html");
        } else if ("setHeader".equals(approach)) {
            response.setHeader("Content-type", "text/html");
        }

        response.getWriter().println("<html><head><title>content-type</title><body>body</body></html>");
    }
}
