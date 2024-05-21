/*
 * Title:        Config
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh;

import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * Common interface to ServletConfig and FilterConfig
 * (since javax.servlet.Config was removed from 2.3 spec).
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.2 $
 */
public class Config {
    private ServletConfig servletConfig;
    private FilterConfig filterConfig;
    private String configFile;
  
    public Config(ServletConfig servletConfig) {
        if (servletConfig == null) throw new NullPointerException("ServletConfig cannot be null");
        this.servletConfig = servletConfig;
        this.configFile = servletConfig.getInitParameter("configFile");
    }

    public Config(FilterConfig filterConfig) {
        if (filterConfig == null) throw new NullPointerException("FilterConfig cannot be null");
        this.filterConfig = filterConfig;
      this.configFile = filterConfig.getInitParameter("configFile");
    }

    public ServletContext getServletContext() {
        return servletConfig != null ? servletConfig.getServletContext() : filterConfig.getServletContext();
    }

    public String getConfigFile() {
        return configFile;
    }
}