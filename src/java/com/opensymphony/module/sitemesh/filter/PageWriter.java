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
import java.io.PrintWriter;

/**
 * Implementation of PrintWriter that stores all data written
 * to it in a temporary buffer accessible from {@link #getBuffer(java.lang.String)} .
 *
 * @author <a href="joe@truemesh.com">Joe Walnes</a>
 * @author <a href="scott@atlassian.com">Scott Farquhar</a>
 * @version $Revision: 1.3 $
 */
public class PageWriter extends PrintWriter implements OutputBuffer {
    private CharArrayWriter buffer = null;
    private PrintWriter bufferWiter = null;
    private PrintWriter original = null;
    private PrintWriter target = null;

    public PageWriter(PrintWriter original) {
        super(original);
        this.original = original;
        buffer = new CharArrayWriter(1000);
        bufferWiter = new PrintWriter(buffer);
        target = bufferWiter;
    }

    public void write(char buf[]) {
        target.write(buf);
    }

    public void write(String s, int off, int len) {
        target.write(s, off, len);
    }

    public void write(String s) {
        target.write(s);
    }

    public void write(char[] c, int o, int l) {
        target.write(c, o, l);
    }

    public void write(int i) {
        target.write(i);
    }

    public void flush() {
        target.flush();
    }

    public void close() {
        target.close();
    }

    public void println() {
        target.println();
    }

    public boolean checkError() {
        return target.checkError();
    }

    public void print(char c) {
        target.print(c);
    }

    public void println(char x) {
        target.println(x);
    }

    public void print(double d) {
        target.print(d);
    }

    public void println(double x) {
        target.println(x);
    }

    public void print(float f) {
        target.print(f);
    }

    public void println(float x) {
        target.println(x);
    }

    public void print(int i) {
        target.print(i);
    }

    public void println(int x) {
        target.println(x);
    }

    public void print(long l) {
        target.print(l);
    }

    public void println(long x) {
        target.println(x);
    }

    public void print(boolean b) {
        target.print(b);
    }

    public void println(boolean x) {
        target.println(x);
    }

    public void print(char s[]) {
        target.print(s);
    }

    public void println(char x[]) {
        target.println(x);
    }

    public void print(Object obj) {
        target.print(obj);
    }

    public void println(Object x) {
        target.println(x);
    }

    public void print(String s) {
        target.print(s);
    }

    public void println(String x) {
        target.println(x);
    }

    /**
     * Return all data that has been written to this Writer.
     */
    public char[] getBuffer(String encoding) {
        return buffer.toCharArray();
    }

    public void discardBuffer() {
        //allow multiple calls to this method without duplicating content to target
        if (target == bufferWiter) {
            target = original;

            // copy any bytes written to buffer so far to original stream.
            original.write(buffer.toCharArray());
            original.flush();
        }
    }
}