package com.opensymphony.module.sitemesh.html;

import com.opensymphony.module.sitemesh.html.util.CharArray;

public abstract class BlockExtractingRule extends BasicRule {

    public void process(Tag tag) {
        if (tag.getType() == Tag.OPEN) {
            bufferStack.pushBuffer(createBuffer());
            start(tag);
        } else if (tag.getType() == Tag.CLOSE) {
            end(tag);
            bufferStack.popBuffer();
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
