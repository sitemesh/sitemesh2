package com.opensymphony.module.sitemesh.html;

import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;

public abstract class BlockExtractingRule extends BasicRule {

    private boolean keepInBuffer;

    // we should only handle tags that have been opened previously.
    // else the parser throws a NoSuchElementException (SIM-216)
    private boolean seenOpeningTag;

    protected BlockExtractingRule(boolean keepInBuffer, String acceptableTagName) {
        super(acceptableTagName);
        this.keepInBuffer = keepInBuffer;
    }

    protected BlockExtractingRule(boolean keepInBuffer) {
        this.keepInBuffer = keepInBuffer;
    }

    public void process(Tag tag) {
        if (tag.getType() == Tag.OPEN) {
            if (!keepInBuffer) {
                context.currentBuffer().markStartDelete(tag.getPosition());
            }
            context.pushBuffer(createBuffer(context.getSitemeshBuffer()).markStart(tag.getPosition() + tag.getLength()));
            start(tag);
            seenOpeningTag = true;
        } else if (tag.getType() == Tag.CLOSE && seenOpeningTag) {
            context.currentBuffer().end(tag.getPosition());
            end(tag);
            context.popBuffer();
            if (!keepInBuffer) {
                context.currentBuffer().endDelete(tag.getPosition() + tag.getLength());
            }
        } else if (!keepInBuffer) {
            context.currentBuffer().delete(tag.getPosition(), tag.getLength());
        }
    }

    protected void start(Tag tag) {
    }

    protected void end(Tag tag) {
    }

    protected SitemeshBufferFragment.Builder createBuffer(SitemeshBuffer sitemeshBuffer) {
        return SitemeshBufferFragment.builder().setBuffer(sitemeshBuffer);
    }

}
