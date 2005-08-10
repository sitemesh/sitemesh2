package com.opensymphony.module.sitemesh.multipass;

import com.opensymphony.module.sitemesh.html.BasicRule;
import com.opensymphony.module.sitemesh.html.State;
import com.opensymphony.module.sitemesh.html.Tag;
import com.opensymphony.module.sitemesh.html.rules.PageBuilder;
import com.opensymphony.module.sitemesh.html.util.CharArray;
import com.opensymphony.module.sitemesh.parser.HTMLPageParser;

public class DivExtractingPageParser extends HTMLPageParser {

    protected void addUserDefinedRules(State html, final PageBuilder page) {
        super.addUserDefinedRules(html, page);
        html.addRule(new TopLevelDivExtractingRule(page));
    }

    private static class TopLevelDivExtractingRule extends BasicRule {
        private String blockId;
        private int depth;
        private final PageBuilder page;

        public TopLevelDivExtractingRule(PageBuilder page) {
            super("div");
            this.page = page;
        }

        public void process(Tag tag) {
            if (tag.getType() == Tag.OPEN) {
                String id = tag.getAttributeValue("id", false);
                if (depth == 0 && id != null) {
                    currentBuffer().append("<sitemesh:multipass id=\"div." + id + "\"/>");
                    blockId = id;
                    context.pushBuffer(new CharArray(512));
                }
                tag.writeTo(currentBuffer());
                depth++;
            } else if (tag.getType() == Tag.CLOSE) {
                depth--;
                tag.writeTo(currentBuffer());
                if (depth == 0 && blockId != null) {
                    page.addProperty("div." + blockId, currentBuffer().toString());
                    blockId = null;
                    context.popBuffer();
                }
            }
        }
    }
}
