/*
 * Title:        ParameterDecoratorMapper
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

import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

/**
 * The ParameterDecoratorMapper will map a suitable decorator based on request
 * parameters.
 *
 * <p>The ParameterDecoratorMapper is configured via three properties.</p>
 *
 * <p><code>decorator.parameter</code> - the parameter which contains the name of the decorator which will be mapped.
 * The default is "decorator".</p>
 *
 * <p>For example if <code>decorator.parameter</code> is "foobar" then
 * myurl.jsp?foobar=mydecorator will map to the decorator named "mydecorator".</p>
 *
 * <p>You can also supply an optional 'confirmation parameter'.
 * The decorator will only be mapped if the parameter named <code>parameter.name</code> is
 * in the request URI and the value of that parameter is equal to the
 * <code>parameter.value</code> property.</p>
 *
 * <p>For example assuming parameter.name=confirm and parameter.value=true
 * the URI myurl.jsp?decorator=mydecorator&confirm=true will map the decorator mydecorator.
 * where as the URIs myurl.jsp?decorator=mydecorator and myurl.jsp?decorator=mydecorator&confirm=false will
 * not return any decorator.</p>
 *
 * @author <a href="mailto:mcannon@internet.com">Mike Cannon-Brookes</a>
 * @version $Revision: 1.3 $
 *
 * @see com.opensymphony.module.sitemesh.DecoratorMapper
 */
public class ParameterDecoratorMapper extends AbstractDecoratorMapper {
    private String decoratorParameter = null, paramName = null, paramValue = null;

    public void init(Config config, Properties properties, DecoratorMapper parent) throws InstantiationException {
        super.init(config, properties, parent);
        decoratorParameter = properties.getProperty("decorator.parameter", "decorator");
        paramName = properties.getProperty("parameter.name", null);
        paramValue = properties.getProperty("parameter.value", null);
    }

    public Decorator getDecorator(HttpServletRequest request, Page page) {
        Decorator result = null;
        String decoratorParamValue = request.getParameter(decoratorParameter);

        if ((paramName == null || paramValue.equals(request.getParameter(paramName)))
            && decoratorParamValue != null && !decoratorParamValue.trim().equals("")) {
                result = getNamedDecorator(request, decoratorParamValue);
        }
        return result == null ? super.getDecorator(request, page) : result;
    }
}