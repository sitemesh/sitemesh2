/* This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file. */
package com.opensymphony.module.sitemesh.filter;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Will wrap a request for the {@link RequestDispatcherWrapper}.
 *
 * @author <a href="mailto:joeo@enigmastation.com">Joseph B. Ottinger</a>
 * @version $Revision: 1.3 $
 */
public class PageRequestWrapper extends HttpServletRequestWrapper {
    boolean debug = false;

    public PageRequestWrapper(HttpServletRequest request) {
        this(request, false);
    }

    public PageRequestWrapper(HttpServletRequest request, boolean debug) {
        super(request);
        this.debug = debug;
    }

    public RequestDispatcher getRequestDispatcher(String s) {
        return new RequestDispatcherWrapper(super.getRequestDispatcher(s));
    }
}
