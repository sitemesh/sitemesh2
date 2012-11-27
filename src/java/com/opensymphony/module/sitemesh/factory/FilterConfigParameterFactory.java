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

    protected long longVal(String parameterName, long defaultVal)
    {
        String num = trimmed(filterConfig.getInitParameter(parameterName));
        long val = defaultVal;
        if (! num.isEmpty())
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

    protected int intVal(String parameterName, int defaultVal)
    {
        String num = trimmed(filterConfig.getInitParameter(parameterName));
        int val = defaultVal;
        if (! num.isEmpty())
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

    protected boolean booleanVal(String parameterName, boolean defaultVal)
    {
        String flag = trimmed(filterConfig.getInitParameter(parameterName));
        boolean val = defaultVal;
        if (! flag.isEmpty())
        {
            try
            {
                val = Boolean.parseBoolean(flag);
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
