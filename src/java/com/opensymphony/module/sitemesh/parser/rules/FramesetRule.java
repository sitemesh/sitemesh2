package com.opensymphony.module.sitemesh.parser.rules;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.html.BasicRule;
import com.opensymphony.module.sitemesh.html.Tag;

public class FramesetRule extends BasicRule {

    private final HTMLPage page;

    public FramesetRule(HTMLPage page) {
        this.page = page;
    }

    public void process(Tag tag) {
        page.setFrameSet(true);
    }

    public boolean shouldProcess(String name) {
        return name.toLowerCase().startsWith("frame");
    }
}
