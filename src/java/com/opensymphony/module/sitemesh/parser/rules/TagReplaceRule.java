package com.opensymphony.module.sitemesh.parser.rules;

import com.opensymphony.module.sitemesh.html.BasicRule;
import com.opensymphony.module.sitemesh.html.Tag;
import com.opensymphony.module.sitemesh.html.util.CharArray;

/**
 * Very simple rule for replacing all occurences of one tag with another.
 *
 * <p>For example, to convert all &lt;b&gt; tags to &lt;strong&gt;:</p>
 * <p>html.addRule(new TagReplaceRule("b", "strong"));</p>
 *
 * <p>Note, attributes are ignored.</p>
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
        // TODO: copy attributes as well
        CharArray buffer = context.currentBuffer();
        switch (tag.getType()) {
            case Tag.OPEN:
                buffer.append("<" + newTagName + ">");
                break;
            case Tag.CLOSE:
                buffer.append("</" + newTagName + ">");
                break;
            case Tag.EMPTY:
                buffer.append("<" + newTagName + "/>");
                break;
            default:
                tag.writeTo(buffer); // leave as is
        }
    }
}
