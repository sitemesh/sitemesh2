/*
 * Title:        RequestConstants
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh;

/**
 * A set of static constants of Strings to be used as ServletRequest attribute keys
 * to represent various objects passed between pages.
 *
 * @author <a href="joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.3 $
 *
 * @see com.opensymphony.sitemesh.webapp.SiteMeshFilter
 * @see com.opensymphony.module.sitemesh.taglib.page.ApplyDecoratorTag
 * @see com.opensymphony.module.sitemesh.mapper.InlineDecoratorMapper
 * @see com.opensymphony.module.sitemesh.mapper.RobotDecoratorMapper
 */
public interface RequestConstants {

    /**
     * Stores {@link com.opensymphony.module.sitemesh.Page} instance for parsed page to be
     * passed across to {@link com.opensymphony.module.sitemesh.Decorator}.
     */
    String PAGE           = "__sitemesh__page";

    /**
     * The name (String) of the Decorator to suggest using. This is set by
     * the {@link com.opensymphony.module.sitemesh.taglib.page.ApplyDecoratorTag}
     * and used by the corresponding {@link com.opensymphony.module.sitemesh.DecoratorMapper}.
     */
    String DECORATOR      = "__sitemesh__decorator";

    /**
     * Marker that stores a Boolean (under the session) to state whether the current
     * session is the session of a web search engine spider. This is set and used by the
     * {@link com.opensymphony.module.sitemesh.mapper.RobotDecoratorMapper}.
     */
    String ROBOT          = "__sitemesh__robot";

}