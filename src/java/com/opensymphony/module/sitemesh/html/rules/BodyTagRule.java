package com.opensymphony.module.sitemesh.html.rules;

import com.opensymphony.module.sitemesh.html.BasicRule;
import com.opensymphony.module.sitemesh.html.Tag;
import com.opensymphony.module.sitemesh.html.util.CharArray;

public class BodyTagRule extends BasicRule {

    private final PageBuilder page;
    private final CharArray body;

    public BodyTagRule(PageBuilder page, CharArray body) {
        super("body");
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
            context.pushBuffer(new CharArray(64)); // unused buffer: everything after </body> is discarded.
        }
    }

}
