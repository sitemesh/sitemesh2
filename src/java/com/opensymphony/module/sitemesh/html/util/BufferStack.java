package com.opensymphony.module.sitemesh.html.util;

import java.util.LinkedList;

public class BufferStack {

    private LinkedList bufferStack = new LinkedList();

    public void pushBuffer(CharArray buffer) {
        bufferStack.add(buffer);
    }

    public CharArray current() {
        return (CharArray) bufferStack.getLast();
    }

    public CharArray popBuffer() {
        return (CharArray) bufferStack.removeLast();
    }
}
