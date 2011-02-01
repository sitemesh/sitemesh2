package com.opensymphony.module.sitemesh.parser;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class SuperFastPage extends AbstractPage {

    private final int bodyStart;
    private final int bodyLength;
    private final int pageLength;


    public SuperFastPage(char[] pageData, int pageLength, int bodyStart, int bodyLength) {
        this.pageLength = pageLength;
        this.bodyStart = bodyStart;
        this.bodyLength = bodyLength;
        this.pageData = pageData;
    }

    @Override
    public void writePage(Writer out) throws IOException {
        out.write(pageData, 0, pageLength);
    }

    @Override
    public int getContentLength() {
        // We encode it but not into a new buffer
        CountingOutputStream counter = new CountingOutputStream();
        try {
            writePage(new OutputStreamWriter(counter));
        } catch (IOException ioe) {
            // Ignore, it's not possible with our OutputStream
        }
        return counter.getCount();
    }

    @Override
    public void writeBody(Writer out) throws IOException {
        out.write(pageData, bodyStart, bodyLength);
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
