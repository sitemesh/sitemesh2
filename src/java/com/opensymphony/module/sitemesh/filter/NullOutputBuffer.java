package com.opensymphony.module.sitemesh.filter;

/**
 * An outputBuffer that does nothing.  If we cannot determine whether getWriter or
 * getOutputStream have been called, then return this object instead.
 *
 * @see PageWriter
 * @see PageOutputStream
 * @see PageResponseWrapper#getBufferStream
 * @author <a href="mailto:scott@atlassian.com">Scott Farquhar</a>
 * @version $Revision: 1.1 $
 */
public class NullOutputBuffer implements OutputBuffer {

    private static NullOutputBuffer nullOutputBuffer = new NullOutputBuffer();

    private NullOutputBuffer() {
    }

    public static NullOutputBuffer getInstance() {
        return nullOutputBuffer;
    }

    public void discardBuffer() {
        //do nothing
    }

    public char[] getBuffer() {
        return new char[0];
    }

    public void flush() {
        //do nothing
    }
}
