package com.opensymphony.module.sitemesh.filter;


/**
 * An interface that bridges PageWriter and PageOutputStream.  Either can be cast to
 * OutputBuffer and used.
 *
 * @see PageWriter
 * @see PageOutputStream
 * @see PageResponseWrapper#getBufferStream
 * @author <a href="mailto:scott@atlassian.com">Scott Farquhar</a>
 * @version $Revision: 1.1 $
 */
public interface OutputBuffer {

    public void discardBuffer();

    public char[] getBuffer();

    public void flush();
}
