package com.opensymphony.module.sitemesh.html;

import com.opensymphony.module.sitemesh.html.util.BufferStack;

public abstract class BasicRule implements TagRule {

    protected BufferStack bufferStack;

    public void setBufferStack(BufferStack bufferStack) {
        this.bufferStack = bufferStack;
    }

    public abstract void process(Tag tag);

}
