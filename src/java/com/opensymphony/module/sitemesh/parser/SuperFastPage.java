package com.opensymphony.module.sitemesh.parser;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshWriter;

public class SuperFastPage extends AbstractPage {

    private final SitemeshBuffer sitemeshBuffer;
    private final int bodyStart;
    private final int bodyLength;


    public SuperFastPage(SitemeshBuffer sitemeshBuffer, int bodyStart, int bodyLength) {
        this.sitemeshBuffer = sitemeshBuffer;
        this.bodyStart = bodyStart;
        this.bodyLength = bodyLength;
        this.pageData = sitemeshBuffer.getCharArray();
    }

    @Override
    public void writePage(Writer out) throws IOException {
        sitemeshBuffer.writeTo(out, 0, sitemeshBuffer.getBufferLength());
    }

    @Override
    public int getContentLength() {
        // We encode it but not into a new buffer
        CountingOutputStream counter = new CountingOutputStream();
        try
        {
            OutputStreamWriter writer = new OutputStreamWriter(counter);
            writePage(writer);
            // We mush flush, because the writer will buffer
            writer.flush();
        } catch (IOException ioe) {
            // Ignore, it's not possible with our OutputStream
        }
        return counter.getCount();
    }

    @Override
    public void writeBody(Writer out) throws IOException {
        if (out instanceof SitemeshWriter) {
            ((SitemeshWriter) out).writeSitemeshBuffer(sitemeshBuffer, bodyStart, bodyLength);
        } else {
            sitemeshBuffer.writeTo(out, bodyStart, bodyLength);
        }
    }

    protected static class CountingOutputStream extends OutputStream {
        private int count = 0;

        @Override
        public void write(int i) throws IOException {
            count++;
        }

        public int getCount() {
            return count;
        }
    }
}
