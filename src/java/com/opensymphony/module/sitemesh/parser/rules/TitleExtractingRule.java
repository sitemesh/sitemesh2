package com.opensymphony.module.sitemesh.parser.rules;

import com.opensymphony.module.sitemesh.html.BlockExtractingRule;
import com.opensymphony.module.sitemesh.html.Tag;
import com.opensymphony.module.sitemesh.parser.PageBuilder;

public class TitleExtractingRule extends BlockExtractingRule {

    private final PageBuilder page;

    private boolean seenTitle;

    public TitleExtractingRule(PageBuilder page) {
        super(false, "title");
        this.page = page;
    }

    protected void end(Tag tag) {
        if (!seenTitle) {
            page.addProperty("title", currentBuffer().toString());
            seenTitle = true;
        }
    }
}
