/* This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file. */
package com.opensymphony.module.sitemesh.filter;

import com.opensymphony.module.sitemesh.*;
import com.opensymphony.module.sitemesh.util.FastByteArrayOutputStream;

import javax.servlet.ServletOutputStream;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * When SiteMesh is activated for a request, the contents of the response are stored in this buffer, where they can
 * later be accessed as a parsed Page object.
 *
 * @author Joe Walnes
 * @version $Revision: 1.3 $
 */
public class Buffer {

    private final PageParser pageParser;
    private final String encoding;
    private final static TextEncoder TEXT_ENCODER = new TextEncoder();

    private final FastByteArrayOutputStream bufferedStream = new FastByteArrayOutputStream();
    private final SitemeshBufferWriter bufferedWriter = new SitemeshBufferWriter(128);
    private PrintWriter exposedWriter;
    private ServletOutputStream exposedStream;

    private boolean usingWriter = false;
    private boolean usingOutputStream = false;

    public Buffer(PageParser pageParser, String encoding) {
        this.pageParser = pageParser;
        this.encoding = encoding;
    }

    public SitemeshBuffer getContents() throws IOException {
        if (bufferedWriter.size() > 0) {
            return bufferedWriter.getSitemeshBuffer();
        } else if (bufferedStream.size() > 0) {
            return new DefaultSitemeshBuffer(TEXT_ENCODER.encode(bufferedStream.toByteArray(), encoding));
        } else {
            return new DefaultSitemeshBuffer(new char[0]);
        }
    }

    public Page parse() throws IOException {
        return pageParser.parse(getContents());
    }

    public PrintWriter getWriter() {
        if (usingOutputStream) {
            throw new IllegalStateException("response.getWriter() called after response.getOutputStream()");
        }
        if (exposedWriter == null) {
            exposedWriter = new SitemeshPrintWriter(bufferedWriter);
        }
        usingWriter = true;
        return exposedWriter;
    }

    public ServletOutputStream getOutputStream() {
        if (usingWriter) {
            throw new IllegalStateException("response.getOutputStream() called after response.getWriter()");
        }
        if (exposedStream == null) {
            exposedStream = new ServletOutputStream() {

                @Override
                public void write(int b) {
                    bufferedStream.write(b);
                }

                @Override
                public void write(byte[] b, int off, int len) throws IOException {
                    bufferedStream.write(b, off, len);
                }
            };
        }
        usingOutputStream = true;
        return exposedStream;
    }

    public boolean isUsingStream() {
        return usingOutputStream;
    }

    public void resetBuffer() {
        bufferedWriter.reset();
        bufferedStream.reset();
    }

    public void reset() {
        usingOutputStream = false;
        usingWriter = false;
        resetBuffer();
    }
}
