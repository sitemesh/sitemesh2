package com.opensymphony.sitemesh.webapp.decorator;

import javax.servlet.ServletContext;

/**
 * Decorator that dispatches to another path in A DIFFERENT WEB-APP in the same Servlet Container (such as a JSP or path mapped to a Servlet).
 * <p/>
 * The Content and SiteMeshContext objects are passed to the decorator using the HttpServletRequest attributes
 * {@link #CONTENT_KEY} and {@link #CONTEXT_KEY}.
 * <p/>
 * To dispatch to a decorator in the same web-app, use {@link DispatchedDecorator}.
 *
 * @author Joe Walnes
 * @since SiteMesh 3.0
 */
public class ExternalDispatchedDecorator extends DispatchedDecorator {

    private final String webApp;

    public ExternalDispatchedDecorator(String path, String webApp) {
        super(path);
        this.webApp = webApp;
    }

    protected ServletContext locateWebApp(ServletContext context) {
        ServletContext externalContext = context.getContext(webApp);
        if (externalContext != null) {
            return externalContext;
        } else {
            // in a security conscious environment, the servlet container
            // may return null for a given URL
            throw new SecurityException("Cannot obtain ServletContext for web-app : " + webApp);
        }
    }
}
