package com.opensymphony.module.sitemesh.parser.rules;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.html.BasicRule;
import com.opensymphony.module.sitemesh.html.Tag;
import com.opensymphony.module.sitemesh.html.util.CharArray;

public class BodyTagRule extends BasicRule {

    private final HTMLPage page;
    private final CharArray body;

    public BodyTagRule(HTMLPage page, CharArray body) {
        this.page = page;
        this.body = body;
    }

    public void process(Tag tag) {
        if (tag.getType() == Tag.OPEN || tag.getType() == Tag.EMPTY) {
            for (int i = 0; i < tag.getAttributeCount(); i++) {
                page.addProperty("body." + tag.getAttributeName(i), tag.getAttributeValue(i));
            }
            body.clear();
        } else {
            bufferStack.pushBuffer(new CharArray(64)); // unused buffer: everything after </body> is discarded.
        }
    }

}
