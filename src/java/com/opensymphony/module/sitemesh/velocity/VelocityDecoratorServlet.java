/*
 * Title:        VelocityDecoratorServlet
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.velocity;

import java.util.Properties;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.servlet.VelocityServlet;
import org.apache.velocity.exception.ResourceNotFoundException;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.RequestConstants;
import com.opensymphony.module.sitemesh.Factory;
import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.util.OutputConverter;

/**
 * Servlet that allows Velocity templates to be used as decorators.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.2 $
 */
public class VelocityDecoratorServlet extends VelocityServlet {
    protected Properties loadConfiguration(ServletConfig config) throws IOException, FileNotFoundException {
        String propsFile = config.getInitParameter(INIT_PROPS_KEY);
        propsFile = getServletContext().getRealPath(propsFile);
        Properties p = new Properties();
        p.load(new FileInputStream(propsFile));
        {
            String path = p.getProperty("file.resource.loader.path");
            p.setProperty("file.resource.loader.path", getServletContext().getRealPath(path));
            path = p.getProperty("runtime.log");
            p.setProperty("runtime.log", getServletContext().getRealPath(path));
        }
        return p;
    }

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
        try {
            return getTemplate(template);
        }
        catch (ResourceNotFoundException e) {
            response.sendError(404);
            return null;
        }
    }
}