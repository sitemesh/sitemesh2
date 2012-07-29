package com.opensymphony.module.sitemesh.outputlength;

import javax.servlet.FilterConfig;

/**
 */
public class OutputLengthObserverFactory
{
    private final long maxOutputLength;
    private final int maximumOutputExceededHttpCode;

    public OutputLengthObserverFactory(FilterConfig filterConfig)
    {
        maxOutputLength = getLongVal(filterConfig, "maximumOutputLength", -1);
        maximumOutputExceededHttpCode = getIntVal(filterConfig, "maximumOutputExceededHttpCode", 509);
    }

    private long getLongVal(FilterConfig filterConfig, String parameterName, int defaultVal)
    {
        String num = filterConfig.getInitParameter(parameterName);
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

    private int getIntVal(FilterConfig filterConfig, String parameterName, int defaultVal)
    {
        String num = filterConfig.getInitParameter(parameterName);
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

    public OutputLengthObserver getObserver()
    {
        if (maxOutputLength > 0)
        {
            return new ObservantObserver(maxOutputLength, maximumOutputExceededHttpCode);
        }
        else
        {
            return new NoopOutputLengthObserver();
        }
    }

    private static class ObservantObserver implements OutputLengthObserver
    {
        private final long maxOutputLength;
        private final int maximumOutputExceededHttpCode;
        private long soFar;


        private ObservantObserver(final long maxOutputLength, final int maximumOutputExceededHttpCode)
        {
            this.maxOutputLength = maxOutputLength;
            this.maximumOutputExceededHttpCode = maximumOutputExceededHttpCode;
        }

        public void nBytes(int n)
        {
            if (soFar + n > maxOutputLength)
            {
                throw new MaxOutputLengthExceeded(maxOutputLength, maximumOutputExceededHttpCode);
            }
        }

        public void nChars(int n)
        {
            if (soFar + (n * 2) > maxOutputLength)
            {
                throw new MaxOutputLengthExceeded(maxOutputLength, maximumOutputExceededHttpCode);
            }
        }
    }
}
