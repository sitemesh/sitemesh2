package com.opensymphony.module.sitemesh.parser.rules;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.html.BlockExtractingRule;
import com.opensymphony.module.sitemesh.html.Tag;

public class ContentBlockExtractingRule extends BlockExtractingRule {

    private final HTMLPage page;

    private String contentBlockId;

    public ContentBlockExtractingRule(HTMLPage page) {
        super(false, "content");
        this.page = page;
    }

    protected void start(Tag tag) {
        contentBlockId = tag.getAttributeValue("tag");
    }

    protected void end(Tag tag) {
        page.addProperty("page." + contentBlockId, context.currentBuffer().toString());
    }

}
