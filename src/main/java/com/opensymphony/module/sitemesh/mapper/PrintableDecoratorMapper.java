/*
 * Title:        PrintableDecoratorMapper
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
 * The PrintableDecoratorMapper is a sample DecoratorMapper that will
 * check to see whether 'printable=true' is supplied as a request parameter
 * and if so, use the specified decorator instead. The name of this decorator
 * should be supplied in the <code>decorator</code> property.
 *
 * <p>The exact 'printable=true' request criteria can be overriden with the
 * <code>parameter.name</code> and <code>parameter.value</code> properties.</p>
 *
 * <p>Although this DecoratorMapper was designed for creating printable versions
 * of a page, it can be used for much more imaginative purposes.</p>
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.2 $
 *
 * @see com.opensymphony.module.sitemesh.DecoratorMapper
 */
public class PrintableDecoratorMapper extends AbstractDecoratorMapper {
    private String decorator, paramName, paramValue;

    public void init(Config config, Properties properties, DecoratorMapper parent) throws InstantiationException {
        super.init(config, properties, parent);
        decorator  = properties.getProperty("decorator");
        paramName  = properties.getProperty("parameter.name", "printable");
        paramValue = properties.getProperty("parameter.value", "true");
    }

    public Decorator getDecorator(HttpServletRequest request, Page page) {
        Decorator result = null;
        if (decorator != null && paramValue.equalsIgnoreCase(request.getParameter(paramName))) {
            result = getNamedDecorator(request, decorator);
        }
        return result == null ? super.getDecorator(request, page) : result;
    }
}