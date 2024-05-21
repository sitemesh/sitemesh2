/*
 * Title:        NullDecoratorMapper
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.mapper;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.DecoratorMapper;
import com.opensymphony.module.sitemesh.Page;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Properties;

/**
 * The NullDecoratorMapper represents the top-level DecoratorMapper that
 * is finally delegated to if no other DecoratorMapper has intervened.
 * It is used so the parent property does not have to be checked by
 * other DecoratorMappers (null object pattern).
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.1 $
 *
 * @see com.opensymphony.module.sitemesh.DecoratorMapper
 */
public class NullDecoratorMapper implements DecoratorMapper {

    /** Does nothing. */
    public void init(Config config, Properties properties, DecoratorMapper parent) {
    }

    /** Returns null. */
    public Decorator getDecorator(HttpServletRequest request, Page page) {
        return null;
    }

    /** Returns null. */
    public Decorator getNamedDecorator(HttpServletRequest request, String name) {
        return null;
    }
}