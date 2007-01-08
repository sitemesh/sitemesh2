package com.opensymphony.module.sitemesh.html;

import com.opensymphony.module.sitemesh.html.util.CharArray;

public abstract class BlockExtractingRule extends BasicRule {

    private boolean includeEnclosingTags;

    // we should only handle tags that have been opened previously.
    // else the parser throws a NoSuchElementException (SIM-216)
    private boolean seenOpeningTag;

    protected BlockExtractingRule(boolean includeEnclosingTags, String acceptableTagName) {
        super(acceptableTagName);
        this.includeEnclosingTags = includeEnclosingTags;
    }

    protected BlockExtractingRule(boolean includeEnclosingTags) {
        this.includeEnclosingTags = includeEnclosingTags;
    }

    public void process(Tag tag) {
        if (tag.getType() == Tag.OPEN) {
            if (includeEnclosingTags) {
                tag.writeTo(context.currentBuffer());
            }
            context.pushBuffer(createBuffer());
            start(tag);
            seenOpeningTag = true;
        } else if (tag.getType() == Tag.CLOSE && seenOpeningTag) {
            end(tag);
            context.popBuffer();
            if (includeEnclosingTags) {
                tag.writeTo(context.currentBuffer());
            }
        }
    }

    protected void start(Tag tag) {
    }

    protected void end(Tag tag) {
    }

    protected CharArray createBuffer() {
        return new CharArray(512);
    }

}
