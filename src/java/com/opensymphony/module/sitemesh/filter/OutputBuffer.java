package com.opensymphony.module.sitemesh.filter;


/**
 * An interface that bridges PageWriter and PageOutputStream.  Either can be cast to
 * OutputBuffer and used.
 *
 * @see PageWriter
 * @see PageOutputStream
 * @see PageResponseWrapper#getBufferStream
 * @author <a href="mailto:scott@atlassian.com">Scott Farquhar</a>
 * @version $Revision: 1.2 $
 */
public interface OutputBuffer {

    public void discardBuffer();

    /**
     * @param encoding The encoding to use for decoding the buffer
     */
    public char[] getBuffer(String encoding);

    public void flush();
}
