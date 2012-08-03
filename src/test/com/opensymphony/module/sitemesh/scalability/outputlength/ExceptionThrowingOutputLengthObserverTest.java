package com.opensymphony.module.sitemesh.scalability.outputlength;

import junit.framework.TestCase;

/**
 */
public class ExceptionThrowingOutputLengthObserverTest extends TestCase
{
    public void testNBytes() throws Exception
    {
        ExceptionThrowingOutputLengthObserver observer = new ExceptionThrowingOutputLengthObserver(25, 1234);
        observer.nBytes(3);
        observer.nBytes(6);
        try
        {
            observer.nBytes(100);
            fail("Should have thrown MaxOutputLengthExceeded");
        }
        catch (MaxOutputLengthExceeded expected)
        {
        }
    }

    public void testNChars() throws Exception
    {
        ExceptionThrowingOutputLengthObserver observer = new ExceptionThrowingOutputLengthObserver(10, 1234);
        observer.nChars(4);
        observer.nChars(1);
        try
        {
            observer.nChars(1);
            fail("Should have thrown MaxOutputLengthExceeded");
        }
        catch (MaxOutputLengthExceeded expected)
        {
        }
    }
}
