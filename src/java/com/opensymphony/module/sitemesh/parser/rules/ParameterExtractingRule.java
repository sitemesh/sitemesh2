package com.opensymphony.module.sitemesh.parser.rules;

import com.opensymphony.module.sitemesh.html.BasicRule;
import com.opensymphony.module.sitemesh.html.Tag;
import com.opensymphony.module.sitemesh.parser.PageBuilder;

public class ParameterExtractingRule extends BasicRule{

    private final PageBuilder page;

    public ParameterExtractingRule(PageBuilder page) {
        super("parameter");
        this.page = page;
    }

    public void process(Tag tag) {
        page.addProperty("page." + tag.getAttributeValue("name", false), tag.getAttributeValue("value", false));
    }
}
