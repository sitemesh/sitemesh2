package com.opensymphony.module.sitemesh.parser.rules;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.html.BasicRule;
import com.opensymphony.module.sitemesh.html.Tag;

public class MetaTagRule extends BasicRule {

    private final HTMLPage page;

    public MetaTagRule(HTMLPage page) {
        this.page = page;
    }

    public void process(Tag tag) {
        if (tag.hasAttribute("name")) {
            page.addProperty("meta." + tag.getAttributeValue("name"), tag.getAttributeValue("content"));
        } else if (tag.hasAttribute("http-equiv")) {
            page.addProperty("meta.http-equiv." + tag.getAttributeValue("http-equiv"), tag.getAttributeValue("content"));
        }
        tag.writeTo(context.currentBuffer());
    }
}
