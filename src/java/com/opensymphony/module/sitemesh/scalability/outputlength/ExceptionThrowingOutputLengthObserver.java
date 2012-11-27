package com.opensymphony.module.sitemesh.scalability.outputlength;

/**
* An implementation that throws {@link MaxOutputLengthExceeded} exceptions
*/
public class ExceptionThrowingOutputLengthObserver implements OutputLengthObserver
{
    private final long maxOutputLength;
    private final int maximumOutputExceededHttpCode;
    private long soFar;


    public ExceptionThrowingOutputLengthObserver(final long maxOutputLength, final int maximumOutputExceededHttpCode)
    {
        this.maxOutputLength = maxOutputLength;
        this.maximumOutputExceededHttpCode = maximumOutputExceededHttpCode;
    }

    public void nBytes(long n)
    {
        if (soFar + n > maxOutputLength)
        {
            throw new MaxOutputLengthExceeded(maxOutputLength, maximumOutputExceededHttpCode);
        }
        soFar += n;
    }

    public void nChars(long n)
    {
        if (soFar + (n * 2) > maxOutputLength)
        {
            throw new MaxOutputLengthExceeded(maxOutputLength, maximumOutputExceededHttpCode);
        }
        soFar += (n * 2);
    }
}
