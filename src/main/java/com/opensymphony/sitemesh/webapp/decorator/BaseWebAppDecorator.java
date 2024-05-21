package com.opensymphony.sitemesh.webapp.decorator;

import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.Decorator;
import com.opensymphony.sitemesh.SiteMeshContext;
import com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Convenient base class for all Java web-app based decorators that make use of the Servlet API.
 *
 * @author Joe Walnes
 * @since SiteMesh 3.0
 */
public abstract class BaseWebAppDecorator implements Decorator {

    /**
     * More convenient version of {@link #render(com.opensymphony.sitemesh.Content, com.opensymphony.sitemesh.SiteMeshContext)}
     * suited for Servlet API calls. 
     */
    protected abstract void render(Content content, HttpServletRequest request, HttpServletResponse response,
                                   ServletContext servletContext, SiteMeshWebAppContext webAppContext)
            throws IOException, ServletException;

    public void render(Content content, SiteMeshContext context) {
        SiteMeshWebAppContext webAppContext = (SiteMeshWebAppContext) context;
        try {
            render(content, webAppContext.getRequest(), webAppContext.getResponse(), webAppContext.getServletContext(), webAppContext);
        } catch (IOException e) {
            throw new RuntimeException("Unexpected exception during rendering", e);
        } catch (ServletException e) {
            throw new RuntimeException("Unexpected exception during rendering", e);
        }
    }
}
