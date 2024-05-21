/* This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file. */
package com.opensymphony.module.sitemesh.filter;

import java.io.PrintWriter;
import java.io.IOException;
import java.io.Writer;

import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.SitemeshWriter;

/**
 * Provides a PrintWriter that routes through to another PrintWriter, however the destination
 * can be changed at any point. The destination can be passed in using a factory, so it will not be created
 * until it's actually needed.
 *
 * @author Joe Walnes
 * @version $Revision: 1.1 $
 */
public class RoutablePrintWriter extends PrintWriter implements SitemeshWriter {

    private PrintWriter destination;
    private DestinationFactory factory;

    /**
     * Factory to lazily instantiate the destination.
     */
    public static interface DestinationFactory {
        PrintWriter activateDestination() throws IOException;
    }

    public RoutablePrintWriter(DestinationFactory factory) {
        super(new NullWriter());
        this.factory = factory;
    }

    private PrintWriter getDestination() {
        if (destination == null) {
            try {
                destination = factory.activateDestination();
            } catch (IOException e) {
                setError();
            }
        }
        return destination;
    }

    public void updateDestination(DestinationFactory factory) {
        destination = null;
        this.factory = factory;
    }

    public Writer getUnderlyingWriter()
    {
        return getDestination();
    }

    public void close() {
        getDestination().close();
    }

    public void println(Object x) {
        getDestination().println(x);
    }

    public void println(String x) {
        getDestination().println(x);
    }

    public void println(char x[]) {
        getDestination().println(x);
    }

    public void println(double x) {
        getDestination().println(x);
    }

    public void println(float x) {
        getDestination().println(x);
    }

    public void println(long x) {
        getDestination().println(x);
    }

    public void println(int x) {
        getDestination().println(x);
    }

    public void println(char x) {
        getDestination().println(x);
    }

    public void println(boolean x) {
        getDestination().println(x);
    }

    public void println() {
        getDestination().println();
    }

    public void print(Object obj) {
        getDestination().print(obj);
    }

    public void print(String s) {
        getDestination().print(s);
    }

    public void print(char s[]) {
        getDestination().print(s);
    }

    public void print(double d) {
        getDestination().print(d);
    }

    public void print(float f) {
        getDestination().print(f);
    }

    public void print(long l) {
        getDestination().print(l);
    }

    public void print(int i) {
        getDestination().print(i);
    }

    public void print(char c) {
        getDestination().print(c);
    }

    public void print(boolean b) {
        getDestination().print(b);
    }

    public void write(String s) {
        getDestination().write(s);
    }

    public void write(String s, int off, int len) {
        getDestination().write(s, off, len);
    }

    public void write(char buf[]) {
        getDestination().write(buf);
    }

    public void write(char buf[], int off, int len) {
        getDestination().write(buf, off, len);
    }

    public void write(int c) {
        getDestination().write(c);
    }

    public boolean checkError() {
        return getDestination().checkError();
    }

    public void flush() {
        getDestination().flush();
    }

    public boolean writeSitemeshBufferFragment(SitemeshBufferFragment bufferFragment) throws IOException
    {
        PrintWriter destination = getDestination();
        if (destination instanceof SitemeshWriter) {
            return ((SitemeshWriter) destination).writeSitemeshBufferFragment(bufferFragment);
        } else {
            bufferFragment.writeTo(destination);
            return true;
        }
    }

    public SitemeshBuffer getSitemeshBuffer()
    {
        PrintWriter destination = getDestination();
        if (destination instanceof SitemeshWriter) {
            return ((SitemeshWriter) destination).getSitemeshBuffer();
        } else {
            throw new IllegalStateException("Print writer is not a sitemesh buffer");
        }
    }
}
