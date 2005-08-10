package com.opensymphony.module.sitemesh.multipass;

import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.module.sitemesh.Page;
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

    public Page parse(char[] data) throws IOException {
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
