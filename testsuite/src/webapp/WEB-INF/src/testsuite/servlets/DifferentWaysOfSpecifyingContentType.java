package testsuite.servlets;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

public class DifferentWaysOfSpecifyingContentType extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
