package com.opensymphony.module.sitemesh.parser;

import com.opensymphony.module.sitemesh.HTMLPage;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class SuperFastHtmlPage extends SuperFastPage implements HTMLPage {

    private final char[] head;

    public SuperFastHtmlPage(char[] pageData, int pageLength, int bodyStart, int bodyLength, char[] head, String title, Map<String, String> metaAttributes) {
        super(pageData, pageLength, bodyStart, bodyLength);
        this.head = head;
        if (title != null) {
            addProperty("title", title);
        }
        for (Map.Entry<String, String> attribute : metaAttributes.entrySet()) {
            addProperty("meta." + attribute.getKey(), attribute.getValue());
        }
    }

    public void writeHead(Writer out) throws IOException {
        out.write(head);
    }

    public String getHead() {
        return new String(head);
    }

    public boolean isFrameSet() {
        return false;
    }

    public void setFrameSet(boolean frameset) {
        throw new UnsupportedOperationException();
    }
}
