package com.opensymphony.module.sitemesh.parser.rules;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.html.BasicRule;
import com.opensymphony.module.sitemesh.html.Tag;

public class MetaTagRule extends BasicRule {

    private final HTMLPage page;

    public MetaTagRule(HTMLPage page) {
        super("meta");
        this.page = page;
    }

    public void process(Tag tag) {
        if (tag.hasAttribute("name", false)) {
            page.addProperty("meta." + tag.getAttributeValue("name", false), tag.getAttributeValue("content", false));
        } else if (tag.hasAttribute("http-equiv", false)) {
            page.addProperty("meta.http-equiv." + tag.getAttributeValue("http-equiv", false), tag.getAttributeValue("content", false));
        }
        tag.writeTo(context.currentBuffer());
    }
}
