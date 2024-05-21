/*
 * Title:        OSDecoratorMapper
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
import java.util.Enumeration;
import java.util.Properties;

/**
 * The OSDecoratorMapper will map a suitable decorator based on the operating system
 * of the remote client.
 *
 * <p>OSDecoratorMapper works by checking to see if the "UA-OS" header
 * was sent with the HTTP request. If it was, the class will check the
 * value of the header with all the different os's the user has configured
 * the Decorator Mapper to identify and, if a match is found, routes the
 * request accordingly.  Configuration is done using the sitemesh.xml file.
 * The param name is a string literal (operating system name) you would like
 * to match in the UA-OS header, and the value is what will be appended to the
 * decorator name if the user is using that operating system</p>
 *
 * @author	<a href="mailto:schepdawg@yahoo.com">Adam P. Schepis</a>
 * @version	$Revision: 1.4 $
 *
 * @see com.opensymphony.module.sitemesh.mapper.AbstractDecoratorMapper
 */
public class OSDecoratorMapper extends AbstractDecoratorMapper {
    /**
     * Properties holds the parameters that the object was initialized with.
     */
    protected Properties properties;

    /**
     * Init initializes the OSDecoratorMapper object by setting the parent
     * DecoratorMapper, and loading the initialization properties.
     *
     * @param config	The config file
     * @param properties	An object containing intialization parameters
     * @param parent	The parent DecoratorMapper object
     */
    public void init(Config config, Properties properties, DecoratorMapper parent) throws java.lang.InstantiationException {
        this.properties = properties;
        this.parent = parent;
    }

    /**
     * Attempts to find the correct decorator for Page page based on
     * the UA-OS HTTP header in the request.
     *
     * @param request The HTTP request sent to the server
     * @param page The page SiteMesh is trying to find a decorator for
     *
     * @return A Decorator object that is either the decorator for the identified
     * OS, or the parent DecoratorMapper's decorator
     */
    public Decorator getDecorator(HttpServletRequest request, Page page) {
        String osHeader = request.getHeader("UA-OS");
        if (osHeader == null) return parent.getDecorator(request, page);

        // run through the list of operating systems the application developer listed
        // in sitemesh.xml to see if we have a match to the user's current OS
        for (Enumeration e = properties.propertyNames(); e.hasMoreElements();) {
            String os = (String) e.nextElement();

            // see if the name matches the user's operating system name
            if (osHeader.toLowerCase().indexOf(os.toLowerCase()) != -1) {
                String decoratorName = parent.getDecorator(request, page).getName();
                if (decoratorName != null) {
                    decoratorName += '-' + properties.getProperty(os);
                }
                return getNamedDecorator(request, decoratorName);
            }
        }

        return parent.getDecorator(request, page);
    }
}