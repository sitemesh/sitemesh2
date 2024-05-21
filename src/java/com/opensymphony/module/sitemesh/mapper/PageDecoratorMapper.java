/*
 * Title:        PageDecoratorMapper
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
import java.util.*;

/**
 * The PageDecoratorMapper allows the actual Page to determine the Decorator to be
 * used.
 *
 * <p>The 'meta.decorator' and 'decorator' properties of the page are accessed
 * and if any of them contain the name of a valid Decorator, that Decorator shall
 * be applied.</p>
 *
 * <p>As an example, if HTML is being used, the Decorator could be chosen by using
 * a <code>&lt;html decorator="mydecorator"&gt;</code> root tag <i>or</i> by using a
 * <code>&lt;meta name="decorator" content="mydecorator"&gt;</code> tag in the header.</p>
 *
 * <p>The actual properties to query are specified by passing properties to the mapper using the
 * <code>property.?</code> prefix. As the properties are stored in a Map, each key has to be unique.
 * Example: property.1=decorator, property.2=meta.decorator .</p>
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.2 $
 *
 * @see com.opensymphony.module.sitemesh.DecoratorMapper
 */
public class PageDecoratorMapper extends AbstractDecoratorMapper {
    private List pageProps = null;

    public void init(Config config, Properties properties, DecoratorMapper parent) throws InstantiationException {
        super.init(config, properties, parent);
        pageProps = new ArrayList();
        Iterator i = properties.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            String key = (String) entry.getKey();
            if (key.startsWith("property")) {
                pageProps.add(entry.getValue());
            }
        }
    }

    public Decorator getDecorator(HttpServletRequest request, Page page) {
        Decorator result = null;
        Iterator i = pageProps.iterator();
        while (i.hasNext()) {
            String propName = (String)i.next();
            result = getByProperty(request, page, propName);
            if (result != null) break;
        }
        return result == null ? super.getDecorator(request, page) : result;
    }

    private Decorator getByProperty(HttpServletRequest request, Page p, String name) {
        if (p.isPropertySet(name)) {
            return getNamedDecorator(request, p.getProperty(name));
        }
        return null;
    }
}