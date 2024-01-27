package com.opensymphony.sitemesh.compatability;

import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext;
import com.opensymphony.sitemesh.webapp.decorator.BaseWebAppDecorator;
import com.opensymphony.module.sitemesh.RequestConstants;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Adapts a SiteMesh 2 {@link com.opensymphony.module.sitemesh.Decorator} to a
 * SiteMesh 3 {@link com.opensymphony.sitemesh.Decorator}.
 *
 * @author Joe Walnes
 * @since SiteMesh 3
 */
public class OldDecorator2NewDecorator extends BaseWebAppDecorator implements RequestConstants {

    private final com.opensymphony.module.sitemesh.Decorator oldDecorator;

    public OldDecorator2NewDecorator(com.opensymphony.module.sitemesh.Decorator oldDecorator) {
        this.oldDecorator = oldDecorator;
    }

    protected void render(Content content, HttpServletRequest request, HttpServletResponse response,
                          ServletContext servletContext, SiteMeshWebAppContext webAppContext)
            throws IOException, ServletException {

        request.setAttribute(PAGE, new Content2HTMLPage(content, request));

        // see if the URI path (webapp) is set
        if (oldDecorator.getURIPath() != null) {
            // in a security conscious environment, the servlet container
            // may return null for a given URL
            if (servletContext.getContext(oldDecorator.getURIPath()) != null) {
                servletContext = servletContext.getContext(oldDecorator.getURIPath());
            }
        }
        // get the dispatcher for the decorator
        RequestDispatcher dispatcher = servletContext.getRequestDispatcher(oldDecorator.getPage());
        dispatcher.include(request, response);

        request.removeAttribute(PAGE);
    }

}
