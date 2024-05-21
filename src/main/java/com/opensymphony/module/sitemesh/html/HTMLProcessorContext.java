package com.opensymphony.module.sitemesh.html;

import com.opensymphony.module.sitemesh.html.util.CharArray;

public interface HTMLProcessorContext {

    State currentState();
    void changeState(State newState);

    void pushBuffer(CharArray buffer);
    CharArray currentBuffer();
    CharArray popBuffer();
    void mergeBuffer();
}
