package com.opensymphony.module.sitemesh.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: joeo
 * Date: Jun 5, 2004
 * Time: 3:36:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class PathologicalServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
