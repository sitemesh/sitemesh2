package com.opensymphony.module.sitemesh.html;

import com.opensymphony.module.sitemesh.html.util.BufferStack;

public interface TagRule {
    void setBufferStack(BufferStack bufferStack);
    void process(Tag tag);
}
