package com.opensymphony.sitemesh.webapp.decorator;

import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Decorator that dispatches to another path in the Servlet Container (such as a JSP or path mapped to a Servlet).
 * <p/>
 * The Content and SiteMeshContext objects are passed to the decorator using the HttpServletRequest attributes
 * {@link #CONTENT_KEY} and {@link #CONTEXT_KEY}.
 * <p/>
 * To dispatch to a decorator in another web-app on the same server, use {@link ExternalDispatchedDecorator}.
 *
 * @author Joe Walnes
 * @since SiteMesh 3.0
 */
public class DispatchedDecorator extends BaseWebAppDecorator {

    public static final String CONTENT_KEY = "com.opensymphony.sitemesh.CONTENT";
    public static final String CONTEXT_KEY = "com.opensymphony.sitemesh.CONTEXT";

    private final String path;

    public DispatchedDecorator(String path) {
        this.path = path;
    }

    protected void render(Content content, HttpServletRequest request, HttpServletResponse response,
                          ServletContext servletContext, SiteMeshWebAppContext webAppContext)
            throws IOException, ServletException {
        Object oldContent = request.getAttribute(CONTENT_KEY);
        Object oldWebAppContext = request.getAttribute(CONTEXT_KEY);

        request.setAttribute(CONTENT_KEY, content);
        request.setAttribute(CONTEXT_KEY, webAppContext);

        try {
            RequestDispatcher dispatcher = servletContext.getRequestDispatcher(path);
            dispatcher.include(request, response);
        } finally {
            request.setAttribute(CONTENT_KEY, oldContent);
            request.setAttribute(CONTEXT_KEY, oldWebAppContext);
        }
    }

    protected ServletContext locateWebApp(ServletContext context) {
        // Overriden by ExternalDispatchedDecorator, which finds the context of another web-app.
        return context;
    }
}
