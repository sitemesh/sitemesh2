package com.opensymphony.module.sitemesh;

import java.io.IOException;
import java.io.Writer;

/**
 * A SiteMesh buffer writer.  Provides the ability to defer the writing of a page body until it is finally written to the
 * stream, so it isn't copied from buffer to buffer.
 */
public interface SitemeshWriter {

    /**
     * Because writer is a class and not an interface we need a  way to self reference so
     * we can pass the underlying Writer onto other Writers.
     *
     * @return a self reference to this so it can be wrapped say.
     */
    public Writer getUnderlyingWriter();

    /**
     * Taken from {@link Writer}
     *
     * @param c the character to write
     */
    public void write(int c)  throws IOException;

    /**
     * Taken from {@link Writer}
     *
     * @param chars the characters to write
     * @param off the offset to write from
     * @param len the length to write
     */
    public void write(char[] chars, int off, int len)  throws IOException;

    /**
     * Taken from {@link Writer}
     *
     * @param chars the characters to write
     */
    public void write(char[] chars) throws IOException;

    /**
     * Taken from {@link Writer}
     *
     * @param str the characters to write
     * @param off the offset to write from
     * @param len the length to write
     */
    public void write(String str, int off, int len)  throws IOException;

    /**
     * Taken from {@link Writer}
     *
     * @param str the characters to write
     */
    public void write(String str) throws IOException;

    /**
     * Taken from {@link Writer}
     */
    public void flush() throws IOException;

    /**
     * Taken from {@link Writer}
     */
    public void close() throws IOException;

    /**
     * Write a sitemesh buffer fragment to the writer.  This may not be written immediately, it may be stored and
     * written later, when this buffer is written out to a writer.
     *
     * @param bufferFragment The buffer fragment to write
     * @return True if the buffer was written immediately, or false if it will be written later
     * @throws IOException If an IOException occurred
     */
    boolean writeSitemeshBufferFragment(SitemeshBufferFragment bufferFragment) throws IOException;

    /**
     * Get the underlying buffer for the writer
     *
     * @return The underlying buffer
     */
    public SitemeshBuffer getSitemeshBuffer();
}
