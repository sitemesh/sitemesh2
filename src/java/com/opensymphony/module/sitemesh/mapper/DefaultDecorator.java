/*
 * Title:        DefaultDecorator
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.mapper;

import com.opensymphony.module.sitemesh.Decorator;

import java.util.Map;
import java.util.Iterator;
import java.util.Collections;

/**
 * Default implementation of Decorator. All properties are set by the
 * constructor.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.1 $
 *
 * @see com.opensymphony.module.sitemesh.Decorator
 */
public class DefaultDecorator implements Decorator {
	/** @see #getPage() */
	protected String page = null;

	/** @see #getName() */
	protected String name = null;

    /** @see #getURIPath() */
    protected String uriPath = null;

    /** @see #getRole() */
    protected String role = null;

	/** @see #getInitParameter(java.lang.String) */
	protected Map parameters = null;

	/** Constructor to set name, page and parameters. */
	public DefaultDecorator(String name, String page, Map parameters) {
        this(name, page, null, null, parameters);
	}

	/** Constructor to set all properties. */
	public DefaultDecorator(String name, String page, String uriPath, Map parameters) {
        this(name, page, uriPath, null, parameters);
	}

    /** Constructor to set all properties. */
	public DefaultDecorator(String name, String page, String uriPath, String role, Map parameters) {
		this.name = name;
		this.page = page;
        this.uriPath = uriPath;
        this.role = role;
		this.parameters = parameters;
	}

	/**
     * URI of the Servlet/JSP to dispatch the request to (relative to the
     * web-app context).
	 */
	public String getPage() {
		return page;
	}

	/** Name of Decorator. For information purposes only. */
	public String getName() {
		return name;
	}

    /** URI path of the Decorator. Enables support for decorators defined in seperate web-apps. */
    public String getURIPath() {
        return uriPath;
    }

    /** Role the user has to be in to get this decorator applied. */
    public String getRole() {
        return role;
    }

	/**
	 * Returns a String containing the value of the named initialization parameter,
     * or null if the parameter does not exist.
	 *
	 * @param paramName Key of parameter.
	 * @return Value of parameter or null if not found.
	 */
	public String getInitParameter(String paramName) {
		if (parameters == null || !parameters.containsKey(paramName)) {
		    return null;
        }

		return (String) parameters.get(paramName);
	}

    /**
     * Returns the names of the Decorator's initialization parameters as an Iterator
     * of String objects, or an empty Iterator if the Decorator has no initialization parameters.
	 */
	public Iterator getInitParameterNames() {
		if (parameters == null) {
            // make sure we always return an empty iterator
		    return Collections.EMPTY_MAP.keySet().iterator();
        }

		return parameters.keySet().iterator();
	}
}