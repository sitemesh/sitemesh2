package com.opensymphony.module.sitemesh.mapper;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.DecoratorMapper;
import com.opensymphony.module.sitemesh.Page;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Properties;

/**
 * <p>Will look at a session attribute to find the name of an appropriate decorator to use. If the
 * session attribute is present, the mapper will not do anything and allow the next mapper in the chain
 * to select a decorator.</p>
 * 
 * <p>By default, it will look at the 'decorator' session attribute, however this can be overriden by
 * configuring the mapper with a 'decorator.parameter' property.</p>
 *
 * @author Ricardo Lecheta
 */
public class SessionDecoratorMapper extends AbstractDecoratorMapper {

    private String decoratorParameter = null;

    public void init(Config config, Properties properties, DecoratorMapper parent) throws InstantiationException {
        super.init(config, properties, parent);
        decoratorParameter = properties.getProperty("decorator.parameter", "decorator");
    }

    public Decorator getDecorator(HttpServletRequest request, Page page) {
        Decorator result = null;
        String decorator = (String) request.getSession().getAttribute(decoratorParameter);

        //get decorator from HttpSession
        if (decorator != null) {
            result = getNamedDecorator(request, decorator);
        }
        return result == null ? super.getDecorator(request, page) : result;
    }
}