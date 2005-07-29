package com.opensymphony.module.sitemesh.html;

import com.opensymphony.module.sitemesh.html.tokenizer.TagTokenizer;
import com.opensymphony.module.sitemesh.html.tokenizer.TokenHandler;
import com.opensymphony.module.sitemesh.html.util.CharArray;
import com.opensymphony.module.sitemesh.util.CharArrayWriter;

import java.io.Reader;
import java.io.Writer;
//import java.io.CharArrayWriter;
import java.io.IOException;

public class HTMLProcessor {

    private final char[] in;
    private final CharArray out;
    private final State defaultState = new State();

    private State currentState = defaultState;
    private Writer outStream;

    public HTMLProcessor(char[] in, CharArray out) {
        this.in = in;
        this.out = out;
    }

    public HTMLProcessor(Reader in, Writer out) throws IOException {
        CharArrayWriter inBuffer = new CharArrayWriter();
        char[] buffer = new char[2048];
        int n;
        while (-1 != (n = in.read(buffer))) {
            inBuffer.write(buffer, 0, n);
        }
        this.in = inBuffer.toCharArray();
        this.out = new CharArray(2048);
        this.outStream = out;
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
        TagTokenizer tokenizer = new TagTokenizer(in);
        final HTMLProcessorContext context = new HTMLProcessorContext() {
            public State currentState() {
                return currentState;
            }

            public void changeState(State newState) {
                currentState = newState;
            }

            private CharArray[] buffers = new CharArray[10];
            private int size;

            public void pushBuffer(CharArray buffer) {
                if(size == buffers.length) {
                  CharArray[] newBuffers = new CharArray[buffers.length * 2];
                  System.arraycopy(buffers, 0, newBuffers, 0, buffers.length);
                  buffers = newBuffers;
                }
                buffers[size++] = buffer;
            }
  
            public CharArray currentBuffer() {
                return buffers[size - 1];
            }
  
            public CharArray popBuffer() {
                CharArray last = buffers[size - 1];
                buffers[--size] = null;
                return last;
            }
  
            public void mergeBuffer() {
                CharArray top = buffers[size - 1];
                CharArray nextDown = buffers[size - 2];
                nextDown.append(top);
            }
        };
        context.pushBuffer(out);
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
        if (outStream != null) {
            outStream.write(out.toString());
        }
    }

    public void addTextFilter(TextFilter textFilter) {
        currentState.addTextFilter(textFilter);
    }

}
