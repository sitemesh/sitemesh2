/*
 * Title:        VelocityDecoratorServlet
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.velocity;

import com.opensymphony.module.sitemesh.*;
import com.opensymphony.module.sitemesh.util.OutputConverter;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.io.VelocityWriter;
import org.apache.velocity.tools.view.servlet.VelocityViewServlet;
import org.apache.velocity.util.SimplePool;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Servlet that allows Velocity templates to be used as decorators.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.4 $
 */
public class VelocityDecoratorServlet extends VelocityViewServlet {
    /** Cache of writers. */
    private static SimplePool writerPool = new SimplePool(40);

    public Template handleRequest(HttpServletRequest request, HttpServletResponse response, Context context) throws Exception {
        HTMLPage htmlPage = (HTMLPage) request.getAttribute(RequestConstants.PAGE);
        String template;

        context.put("base", request.getContextPath());
        if (htmlPage == null) {
            context.put("title", "Title?");
            context.put("body", "<p>Body?</p>");
            context.put("head", "<!-- head -->");
            template = request.getServletPath();
        }
        else {
            context.put("title", htmlPage.getTitle());
            {
                StringWriter buffer = new StringWriter();
                htmlPage.writeBody(OutputConverter.getWriter(buffer));
                context.put("body", buffer.toString());
            }
            {
                StringWriter buffer = new StringWriter();
                htmlPage.writeHead(OutputConverter.getWriter(buffer));
                context.put("head", buffer.toString());
            }
            context.put("page", htmlPage);
            Factory factory = Factory.getInstance(new Config(getServletConfig()));
            Decorator decorator = factory.getDecoratorMapper().getDecorator(request, htmlPage);
            template = decorator.getPage();
        }

        return getTemplate(template);
    }

    /**
     * Merges the template with the context.
     *
     * <p>This method has been overridden because the one
     * from {@link VelocityViewServlet} uses response.getOutputStream() instead of
     * response.getWriter().</p>
     *
     * @param template template object returned by the handleRequest() method
     * @param context  context created by the createContext() method
     * @param response servlet reponse (use this to get the output stream or Writer
     */
    protected void mergeTemplate(Template template, Context context, HttpServletResponse response) throws ResourceNotFoundException, ParseErrorException,
            MethodInvocationException, IOException,
            UnsupportedEncodingException, Exception {

        Writer responseWriter = response.getWriter();
        VelocityWriter vw = null;

        try {
            vw = (VelocityWriter) writerPool.get();

            if (vw == null) {
                vw = new VelocityWriter(responseWriter, 4 * 1024, true);
            }
            else {
                vw.recycle(responseWriter);
            }

            template.merge(context, vw);
        }
        finally {
            try {
                if (vw != null) {
                    // flush and put back into the pool
                    // don't close to allow us to play
                    // nicely with others.
                    vw.flush();
                    writerPool.put(vw);
                }
            }
            catch (Exception e) {
                // do nothing
            }
        }
    }

    /**
     * Invoked when there is an error thrown in any part of doRequest() processing.
     * <br><br>
     * Default will send a simple HTML response indicating there was a problem.
     *
     * <p>This method has been overridden because the one
     * from {@link VelocityViewServlet} uses response.getOutputStream() instead of
     * response.getWriter().</p>
     *
     * @param request  original HttpServletRequest from servlet container.
     * @param response HttpServletResponse object from servlet container.
     * @param cause    Exception that was thrown by some other part of process.
     */
    protected void error(HttpServletRequest request, HttpServletResponse response, Exception cause)
            throws ServletException, IOException {
        StringBuffer html = new StringBuffer();
        html.append("<html>");
        html.append("<title>Error</title>");
        html.append("<body bgcolor=\"#ffffff\">");
        html.append("<h2>VelocityDecoratorServlet : Error processing the template</h2>");
        html.append("<pre>");
        String why = cause.getMessage();
        if (why != null && why.trim().length() > 0) {
            html.append(why);
            html.append("<br>");
        }

        StringWriter sw = new StringWriter();
        cause.printStackTrace(new PrintWriter(sw));

        html.append(sw.toString());
        html.append("</pre>");
        html.append("</body>");
        html.append("</html>");
        response.getWriter().print(html.toString());
    }
}