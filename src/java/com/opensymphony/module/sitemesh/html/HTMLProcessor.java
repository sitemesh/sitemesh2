package com.opensymphony.module.sitemesh.html;

import com.opensymphony.module.sitemesh.html.tokenizer.TagTokenizer;
import com.opensymphony.module.sitemesh.html.tokenizer.TokenHandler;
import com.opensymphony.module.sitemesh.html.util.CharArray;

import java.util.LinkedList;

public class HTMLProcessor {

    private final char[] input;
    private final CharArray body;
    private final State defaultState = new State();

    private State currentState = defaultState;

    public HTMLProcessor(char[] input, CharArray body) {
        this.input = input;
        this.body = body;
    }

    public void addRule(String tagName, TagRule rule) {
        defaultState.addRule(tagName, rule);
    }

    public State defaultState() {
        return defaultState;
    }

    public void process() {
        TagTokenizer tokenizer = new TagTokenizer(input);
        final HTMLProcessorContext context = new HTMLProcessorContext() {
            public State currentState() {
                return currentState;
            }

            public void changeState(State newState) {
                currentState = newState;
            }

            private LinkedList bufferStack = new LinkedList();

            public void pushBuffer(CharArray buffer) {
                bufferStack.add(buffer);
            }

            public CharArray currentBuffer() {
                return (CharArray) bufferStack.getLast();
            }

            public CharArray popBuffer() {
                return (CharArray) bufferStack.removeLast();
            }
        };
        context.pushBuffer(body);
        tokenizer.start(new TokenHandler() {

            public boolean shouldProcessTag(String name) {
                return currentState.shouldProcessTag(name);
            }

            public void tag(Tag tag) {
                TagRule tagRule = currentState.getRule(tag.getName());
                tagRule.setContext(context);
                tagRule.process(tag);
            }

            public void text(Text text) {
                text.writeTo(context.currentBuffer());
            }

            public void warning(String message, int line, int column) {
                // TODO
                // System.out.println(line + "," + column + ": " + message);
            }
        });
    }
}
