package com.opensymphony.module.sitemesh.factory;

import javax.servlet.FilterConfig;

/**
 * A base class for factories that know about filterConfig parameters
 */
public abstract class FilterConfigParameterFactory
{
    private final FilterConfig filterConfig;

    protected FilterConfigParameterFactory(FilterConfig filterConfig)
    {
        this.filterConfig = filterConfig;
    }

    protected long getLongVal(String parameterName, long defaultVal)
    {
        String num = trimmed(filterConfig.getInitParameter(parameterName));
        long val = defaultVal;
        if (num != null)
        {
            try
            {
                val = Long.parseLong(num);
            }
            catch (NumberFormatException e)
            {
            }
        }
        return val;
    }

    protected String getStringVal(String parameterName, String defaultVal)
    {
        String s = trimmed(filterConfig.getInitParameter(parameterName));
        if (s == null || s.isEmpty())
        {
            s = defaultVal;
        }
        return s;
    }

    protected int getIntVal(String parameterName, int defaultVal)
    {
        String num = trimmed(filterConfig.getInitParameter(parameterName));
        int val = defaultVal;
        if (num != null)
        {
            try
            {
                val = Integer.parseInt(num);
            }
            catch (NumberFormatException e)
            {
            }
        }
        return val;
    }

    private String trimmed(String s)
    {
        return (s == null ? "" : s).trim();
    }

}
