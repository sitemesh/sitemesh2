package com.opensymphony.module.sitemesh.html;

import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.html.tokenizer.TagTokenizer;
import com.opensymphony.module.sitemesh.html.tokenizer.TokenHandler;

import java.io.IOException;

public class HTMLProcessor {

    private final SitemeshBuffer sitemeshBuffer;
    private final SitemeshBufferFragment.Builder body;
    private final State defaultState = new State();

    private State currentState = defaultState;

    public HTMLProcessor(SitemeshBuffer sitemeshBuffer, SitemeshBufferFragment.Builder body) {
        this.sitemeshBuffer = sitemeshBuffer;
        this.body = body;
    }

    public State defaultState() {
        return defaultState;
    }

    /**
     * Equivalent of htmlProcessor.defaultState().addRule()
     */ 
    public void addRule(TagRule rule) {
        defaultState.addRule(rule);
    }

    public void process() throws IOException {
        TagTokenizer tokenizer = new TagTokenizer(sitemeshBuffer.getCharArray(), sitemeshBuffer.getBufferLength());
        final HTMLProcessorContext context = new HTMLProcessorContext() {

            public SitemeshBuffer getSitemeshBuffer() {
                return sitemeshBuffer;
            }

            public State currentState() {
                return currentState;
            }

            public void changeState(State newState) {
                currentState = newState;
            }

            private SitemeshBufferFragment.Builder[] buffers = new SitemeshBufferFragment.Builder[10];
            private int size;

            public void pushBuffer(SitemeshBufferFragment.Builder buffer) {
                if(size == buffers.length) {
                  SitemeshBufferFragment.Builder[] newBuffers = new SitemeshBufferFragment.Builder[buffers.length * 2];
                  System.arraycopy(buffers, 0, newBuffers, 0, buffers.length);
                  buffers = newBuffers;
                }
                buffers[size++] = buffer;
            }
  
            public SitemeshBufferFragment.Builder currentBuffer() {
                return buffers[size - 1];
            }
  
            public SitemeshBufferFragment.Builder popBuffer() {
                SitemeshBufferFragment.Builder last = buffers[size - 1];
                buffers[--size] = null;
                return last;
            }
        };
        context.pushBuffer(body);
        tokenizer.start(new TokenHandler() {

            public boolean shouldProcessTag(String name) {
                return currentState.shouldProcessTag(name.toLowerCase());
            }

            public void tag(Tag tag) {
                TagRule tagRule = currentState.getRule(tag.getName().toLowerCase());
                tagRule.setContext(context);
                tagRule.process(tag);
            }

            public void text(Text text) {
                currentState.handleText(text, context);
            }

            public void warning(String message, int line, int column) {
                // TODO
                // System.out.println(line + "," + column + ": " + message);
            }
        });
        defaultState.endOfState();
    }

    public void addTextFilter(TextFilter textFilter) {
        currentState.addTextFilter(textFilter);
    }

}
