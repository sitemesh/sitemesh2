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
import org.apache.velocity.tools.view.servlet.VelocityViewServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;

/**
 * Servlet that allows Velocity templates to be used as decorators.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.9 $
 */
public class VelocityDecoratorServlet extends VelocityViewServlet {
    public Template handleRequest(HttpServletRequest request, HttpServletResponse response, Context context) throws Exception {
        HTMLPage htmlPage = (HTMLPage) request.getAttribute(RequestConstants.PAGE);
        String template;

        context.put("base", request.getContextPath());

        // For backwards compatability with apps that used the old VelocityDecoratorServlet
        // that extended VelocityServlet instead of VelocityViewServlet
        context.put("req", request);
        context.put("res", response);

        if (htmlPage == null) {
            context.put("title", "Title?");
            context.put("body", "<p>Body?</p>");
            context.put("head", "<!-- head -->");
            template = request.getServletPath();
        }
        else {
            context.put("title", OutputConverter.convert(htmlPage.getTitle()));
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
            DecoratorMapper decoratorMapper = getDecoratorMapper();
            Decorator decorator = decoratorMapper.getDecorator(request, htmlPage);
            template = decorator.getPage();
        }

        return getTemplate(template);
    }

    private DecoratorMapper getDecoratorMapper() {
        Factory factory = Factory.getInstance(new Config(getServletConfig()));
        DecoratorMapper decoratorMapper = factory.getDecoratorMapper();
        return decoratorMapper;
    }
}