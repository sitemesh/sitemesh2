package testsuite.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ContentLengthServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        int length = Integer.parseInt(request.getParameter("content-length"));
        response.setContentLength(length);
        for (int i=0; i< length; i++)
        {
            response.getWriter().write("a");
        }
    }
}
