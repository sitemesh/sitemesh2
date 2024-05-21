package com.opensymphony.module.sitemesh.html.rules;

import com.opensymphony.module.sitemesh.html.BasicRule;
import com.opensymphony.module.sitemesh.html.Tag;
import com.opensymphony.module.sitemesh.html.CustomTag;

/**
 * Very simple rule for replacing all occurences of one tag with another.
 *
 * <p>For example, to convert all &lt;b&gt; tags to &lt;strong&gt;:</p>
 * <p>html.addRule(new TagReplaceRule("b", "strong"));</p>
 *
 * @author Joe Walnes
 */
public class TagReplaceRule extends BasicRule {

    private final String newTagName;

    public TagReplaceRule(String originalTagName, String newTagName) {
        super(originalTagName);
        this.newTagName = newTagName;
    }

    public void process(Tag tag) {
        CustomTag customTag = new CustomTag(tag);
        customTag.setName(newTagName);
        customTag.writeTo(currentBuffer());
    }
}
