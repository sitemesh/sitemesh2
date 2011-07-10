package com.opensymphony.module.sitemesh.multipass;

import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.html.util.CharArray;
import com.opensymphony.module.sitemesh.html.HTMLProcessor;
import com.opensymphony.module.sitemesh.html.BasicRule;
import com.opensymphony.module.sitemesh.html.Tag;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MultipassReplacementPageParser implements PageParser {
    private final Page page;
    private final HttpServletResponse response;

    public MultipassReplacementPageParser(Page page, HttpServletResponse response) {
        this.page = page;
        this.response = response;
    }

    public Page parse(SitemeshBuffer buffer) throws IOException {
        char[] data;
        int length;
        if (buffer.hasFragments()) {
            // Write the buffer into a char array
            com.opensymphony.module.sitemesh.util.CharArrayWriter writer = new com.opensymphony.module.sitemesh.util.CharArrayWriter(buffer.getTotalLength());
            buffer.writeTo(writer, 0, buffer.getBufferLength());
            data = writer.toCharArray();
            length = data.length;
        } else {
            data = buffer.getCharArray();
            length = buffer.getBufferLength();
        }
        return parse(data, length);
    }

    private Page parse(char[] data, int length) throws IOException
    {
        if (data.length > length) {
            // todo fix this parser so that it doesn't need to compact the array
            char[] newData = new char[length];
            System.arraycopy(data, 0, newData, 0, length);
            data = newData;
        }
        return parse(data);
    }
    
    private Page parse(char[] data) throws IOException {
        final CharArray result = new CharArray(4096);
        HTMLProcessor processor = new HTMLProcessor(data, result);
        processor.addRule(new BasicRule("sitemesh:multipass") {
            public void process(Tag tag) {
                String id = tag.getAttributeValue("id", true);
                if (!page.isPropertySet("_sitemesh.removefrompage." + id)) {
                    currentBuffer().append(page.getProperty(id));
                }
            }
        });
        processor.process();

        result.writeTo(response.getWriter());
        return null;
    }
}
