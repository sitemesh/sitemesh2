package com.opensymphony.module.sitemesh.parser.rules;

import com.opensymphony.module.sitemesh.html.BlockExtractingRule;
import com.opensymphony.module.sitemesh.html.Tag;
import com.opensymphony.module.sitemesh.parser.PageBuilder;

public class ContentBlockExtractingRule extends BlockExtractingRule {

    private final PageBuilder page;

    private String contentBlockId;

    public ContentBlockExtractingRule(PageBuilder page) {
        super(false, "content");
        this.page = page;
    }

    protected void start(Tag tag) {
        contentBlockId = tag.getAttributeValue("tag", false);
    }

    protected void end(Tag tag) {
        page.addProperty("page." + contentBlockId, context.currentBuffer().toString());
    }

}
