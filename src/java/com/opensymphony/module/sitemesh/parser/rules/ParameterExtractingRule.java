package com.opensymphony.module.sitemesh.parser.rules;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.html.BasicRule;
import com.opensymphony.module.sitemesh.html.Tag;

public class ParameterExtractingRule extends BasicRule{

    private final HTMLPage page;

    public ParameterExtractingRule(HTMLPage page) {
        super("parameter");
        this.page = page;
    }

    public void process(Tag tag) {
        page.addProperty("page." + tag.getAttributeValue("name", false), tag.getAttributeValue("value", false));
    }
}
