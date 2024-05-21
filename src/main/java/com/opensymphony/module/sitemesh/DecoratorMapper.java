/*
 * Title:        DecoratorMapper
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Properties;

/**
 * The DecoratorMapper is responsible for determining which
 * {@link com.opensymphony.module.sitemesh.Decorator} should be used for a
 * {@link com.opensymphony.module.sitemesh.Page}.
 *
 * <p>Implementations of this are returned by the {@link com.opensymphony.module.sitemesh.Factory},
 * and should be thread-safe.</p>
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.2 $
 */
public interface DecoratorMapper {
    /**
     * Initialize the mapper. This is always called before the other methods.
     *
     * @param config Config supplied by Servlet or Filter.
     * @param properties Any initialization properties (specific to implementation).
     * @exception java.lang.InstantiationException should be thrown if the implementation
     *            cannot be initialized properly.
     */
    void init(Config config, Properties properties, DecoratorMapper parent) throws InstantiationException;

    /**
     * Return appropriate {@link com.opensymphony.module.sitemesh.Decorator} for a certain Page.
     *
     * <p>The implementation can determine the result based on the actual request
     * or the data of the parsed page. Typically this would call <code>getNamedDecorator()</code>
     * which would delegate to a parent DecoratorMapper.</p>
     *
     */
    Decorator getDecorator(HttpServletRequest request, Page page);

    /** Return a {@link com.opensymphony.module.sitemesh.Decorator} with given name. */
    Decorator getNamedDecorator(HttpServletRequest request, String name);
}