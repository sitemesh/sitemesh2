package com.opensymphony.module.sitemesh.parser.rules;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.html.BasicRule;
import com.opensymphony.module.sitemesh.html.Tag;

public class HtmlAttributesRule extends BasicRule{

    private final HTMLPage page;

    public HtmlAttributesRule(HTMLPage page) {
        this.page = page;
    }

    public void process(Tag tag) {
        if (tag.getType() == Tag.OPEN) {
            for (int i = 0; i < tag.getAttributeCount(); i++) {
                page.addProperty(tag.getAttributeName(i), tag.getAttributeValue(i));
            }
        }
    }
}
