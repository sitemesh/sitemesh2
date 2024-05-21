/* This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file. */
package com.opensymphony.module.sitemesh.filter;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;

/**
 * Special request dispatcher that will include when an inline decorator includes
 * a resource that uses an internal forward.
 *
 * @see com.opensymphony.module.sitemesh.taglib.page.ApplyDecoratorTag
 *
 * @author <a href="mailto:joeo@enigmastation.com">Joseph B. Ottinger</a>
 * @version $Revision: 1.2 $
 */
public class RequestDispatcherWrapper implements RequestDispatcher {
    private RequestDispatcher rd = null;
    private boolean done = false;

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