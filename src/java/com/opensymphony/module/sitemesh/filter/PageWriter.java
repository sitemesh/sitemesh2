/*
 * Title:        PageWriter
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.filter;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * Implementation of PrintWriter that stores all data written
 * to it in a temporary buffer accessible from {@link #getBuffer()} .
 *
 * @author <a href="joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.1 $
 */
public class PageWriter extends PrintWriter implements OutputBuffer {
    private CharArrayWriter buffer = null;
    private Writer original = null;
    private Writer target = null;

    public PageWriter(Writer original) throws IOException {
        super(original);
        this.original = original;
        buffer = new CharArrayWriter(1000);
        target = buffer;
    }

    public void write(char buf[]) {
        try {
            target.write(buf);
        }
        catch (IOException e) {
            setError();
        }
    }

    public void write(String s, int off, int len) {
        try {
            target.write(s, off, len);
        }
        catch (IOException e) {
            setError();
        }
    }

    public void write(String s) {
        try {
            target.write(s);
        }
        catch (IOException e) {
            setError();
        }
    }


    public void write(char[] c, int o, int l) {
        try {
            target.write(c, o, l);
        }
        catch (IOException e) {
            setError();
        }
    }

    public void write(int i) {
        try {
            target.write(i);
        }
        catch (IOException e) {
            setError();
        }
    }

    public void flush() {
        try {
            target.flush();
        }
        catch (IOException e) {
            setError();
        }
    }

    public void close() {
        try {
            target.close();
        }
        catch (IOException e) {
            setError();
        }
    }

    /** Return all data that has been written to this Writer. */
    public char[] getBuffer() {
        buffer.flush();
        return buffer.toCharArray();
    }

    public void discardBuffer() {
        //allow multiple calls to this method without duplicating content to target
        if (target == buffer) {
            target = original;
            try {
                // copy any bytes written to buffer so far to original stream.
                original.write(getBuffer());
                original.flush();
            } catch (IOException e) {
                e.printStackTrace(); // this should never happen.
            }
        }
    }
}