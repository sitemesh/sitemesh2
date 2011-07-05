package com.opensymphony.module.sitemesh;

import java.io.IOException;

/**
 * A sitemesh buffer writer.  Provides the ability to defer the writing of a page body until it is finally written to the
 * stream, so it isn't copied from buffer to buffer
 */
public interface SitemeshWriter {
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
