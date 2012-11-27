package com.opensymphony.module.sitemesh.scalability.outputlength;

/**
 * An exception that is thrown if the maximum output length is exceeded.
 */
public class MaxOutputLengthExceeded extends RuntimeException
{
    private final long maxOutputLength;
    private final int maximumOutputExceededHttpCode;

    public MaxOutputLengthExceeded(final long maxOutputLength, final int maximumOutputExceededHttpCode)
    {
        super("The maximum output length of " + maxOutputLength + " bytes has been exceeded");
        this.maxOutputLength = maxOutputLength;
        this.maximumOutputExceededHttpCode = maximumOutputExceededHttpCode;
    }

    public long getMaxOutputLength()
    {
        return maxOutputLength;
    }

    public int getMaximumOutputExceededHttpCode()
    {
        return maximumOutputExceededHttpCode;
    }
}
