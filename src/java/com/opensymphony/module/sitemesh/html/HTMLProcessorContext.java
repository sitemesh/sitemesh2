package com.opensymphony.module.sitemesh.html;

import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.html.util.CharArray;

public interface HTMLProcessorContext {

    SitemeshBuffer getSitemeshBuffer();

    State currentState();
    void changeState(State newState);

    void pushBuffer(SitemeshBufferFragment.Builder fragment);
    SitemeshBufferFragment.Builder currentBuffer();
    SitemeshBufferFragment.Builder popBuffer();
}
