/*
 * Title:        FrameSetDecoratorMapper
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
import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.Page;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Properties;

/**
 * The FrameSetDecoratorMapper will use the specified decorator when the Page
 * is an instance of {@link com.opensymphony.module.sitemesh.HTMLPage} and
 * <code>isFrameSet()</code> returns true.
 *
 * <p>The name of this decorator should be supplied in the <code>decorator</code>
 * property - if no decorator property is supplied, no decorator is applied to
 * frame based pages.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.2 $
 *
 * @see com.opensymphony.module.sitemesh.DecoratorMapper
 */
public class FrameSetDecoratorMapper extends AbstractDecoratorMapper {
    private String decorator = null;

    public void init(Config config, Properties properties, DecoratorMapper parent) throws InstantiationException {
        super.init(config, properties, parent);
        decorator = properties.getProperty("decorator");
    }

    public Decorator getDecorator(HttpServletRequest request, Page page) {
        if (page instanceof HTMLPage && ((HTMLPage)page).isFrameSet()) {
            return getNamedDecorator(request, decorator);
        }
        else {
            return super.getDecorator(request, page);
        }
    }
}
