package com.opensymphony.module.sitemesh.scalability;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

/**
*/
class MockFilterConfig implements FilterConfig
{

    final Map<String,String> properties;

    MockFilterConfig(Map<String, String> properties)
    {
        this.properties = properties;
    }

    public String getFilterName()
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    public ServletContext getServletContext()
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    public String getInitParameter(String s)
    {
        return properties.get(s);
    }

    public Enumeration getInitParameterNames()
    {
        return new Hashtable<String,String>(properties).keys();
    }
}
