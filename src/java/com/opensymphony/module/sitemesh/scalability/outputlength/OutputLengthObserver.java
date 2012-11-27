package com.opensymphony.module.sitemesh.scalability.outputlength;

/**
 * An interface that observes the length of output as it is written to streams and writers
 * and hence allows action to be taken if its too much
 */
public interface OutputLengthObserver
{
    /**
     * The {@link java.io.OutputStream is about to write n bytes}
     * @param n the number of bytes about to be written
     */
    void nBytes(long n);

    /**
     * The {@link java.io.Writer is about to write n characters}
     * @param n the number of characters about to be written
     */
    void nChars(long n);
}
