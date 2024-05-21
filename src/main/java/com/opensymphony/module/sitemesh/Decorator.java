/*
 * Title:        Decorator
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh;

import java.util.Iterator;

/**
 * Representation of a Decorator.
 *
 * <p>A Decorator is infact a Servlet/JSP, and this is a wrapper to reference it.
 * An implementation is returned by the {@link com.opensymphony.module.sitemesh.DecoratorMapper}.</p>
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.1 $
 */
public interface Decorator {
    /**
     * URI of the Servlet/JSP to dispatch the request to (relative to the
     * web-app context).
     */
    String getPage();

    /** Name of the Decorator. For informational purposes only. */
    String getName();

    /** URI path of the Decorator. Enables support for decorators defined in seperate web-apps. */
    String getURIPath();

    /** Role the user has to be in to get this decorator applied. */
    String getRole();

    /**
     * Returns a String containing the value of the named initialization parameter,
     * or null if the parameter does not exist.
     *
	 * @param paramName Key of parameter.
     * @return Value of the parameter or null if not found.
     */
    String getInitParameter(String paramName);

    /**
     * Returns the names of the Decorator's initialization parameters as an Iterator
     * of String objects, or an empty Iterator if the Decorator has no initialization parameters.
     */
    Iterator getInitParameterNames();
}