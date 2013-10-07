package testsuite.servlets;

import com.opensymphony.module.sitemesh.RequestConstants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class OutputServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ("yes".equals(request.getParameter("kill"))) {
            request.setAttribute(RequestConstants.DISABLE_BUFFER_AND_DECORATION, Boolean.TRUE);
        }

        String mode = request.getParameter("out");
        PrintWriter pw = null;

        if (mode.equals("stream")) {
            OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream());
            pw = new PrintWriter(osw);
        }

        if (pw == null) {
            pw = response.getWriter();
        }
        response.setContentType("text/html");
        pw.println("<html><head><title>Servlet using " + mode + "</title></head>");
        pw.println("<body>This is a servlet using " + mode + " to output</body></html>");
        pw.flush();
    }
}
