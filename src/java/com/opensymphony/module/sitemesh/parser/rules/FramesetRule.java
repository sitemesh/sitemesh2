package com.opensymphony.module.sitemesh.parser.rules;

import com.opensymphony.module.sitemesh.html.BasicRule;
import com.opensymphony.module.sitemesh.html.Tag;
import com.opensymphony.module.sitemesh.parser.PageBuilder;

public class FramesetRule extends BasicRule {

    private final PageBuilder page;

    public FramesetRule(PageBuilder page) {
        super(new String[] {"frame", "frameset"});
        this.page = page;
    }

    public void process(Tag tag) {
        page.addProperty("frameset", "true");
    }

}
