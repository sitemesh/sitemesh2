package com.opensymphony.module.sitemesh.parser.rules;

import com.opensymphony.module.sitemesh.html.BasicRule;
import com.opensymphony.module.sitemesh.html.Tag;
import com.opensymphony.module.sitemesh.parser.PageBuilder;

public class HtmlAttributesRule extends BasicRule{

    private final PageBuilder page;

    public HtmlAttributesRule(PageBuilder page) {
        super("html");
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
