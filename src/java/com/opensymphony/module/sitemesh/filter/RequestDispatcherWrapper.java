package com.opensymphony.module.sitemesh.filter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Special request dispatcher that will include when an inline decorator includes
 * a resource that uses an internal forward.
 *
 * @see com.opensymphony.module.sitemesh.taglib.page.ApplyDecoratorTag
 *
 * @author <a href="mailto:joeo@enigmastation.com">Joseph B. Ottinger</a>
 * @version $Revision: 1.1 $
 */
public class RequestDispatcherWrapper implements RequestDispatcher {
    private RequestDispatcher rd = null;
    boolean done = false;

    public RequestDispatcherWrapper(RequestDispatcher rd) {
        this.rd = rd;
    }

    public void forward(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        if (!done) {
            include(servletRequest, servletResponse);
            done = true;
        }
        else {
            throw new IllegalStateException("Response has already been committed");
        }
    }

    public void include(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        if (!done) {
            rd.include(servletRequest, servletResponse);
        }
        else {
            throw new IllegalStateException("Response has already been committed");
        }
    }
}