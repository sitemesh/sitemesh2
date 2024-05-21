package com.opensymphony.module.sitemesh.html.rules;

import com.opensymphony.module.sitemesh.html.BasicRule;
import com.opensymphony.module.sitemesh.html.Tag;

public class FramesetRule extends BasicRule {

    private final PageBuilder page;

    public FramesetRule(PageBuilder page) {
        super(new String[] {"frame", "frameset"});
        this.page = page;
    }

    public void process(Tag tag) {
        context.currentBuffer().delete(tag.getPosition(), tag.getLength());
        page.addProperty("frameset", "true");
    }

}
