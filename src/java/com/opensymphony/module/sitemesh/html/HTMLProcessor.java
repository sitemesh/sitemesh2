package com.opensymphony.module.sitemesh.html;

import com.opensymphony.module.sitemesh.html.tokenizer.TagTokenizer;
import com.opensymphony.module.sitemesh.html.tokenizer.TokenHandler;
import com.opensymphony.module.sitemesh.html.util.BufferStack;

import java.util.HashMap;
import java.util.Map;

public class HTMLProcessor {

    private final Map rules = new HashMap();
    private final char[] input;
    private final BufferStack bufferStack;

    public HTMLProcessor(char[] input, BufferStack bufferStack) {
        this.input = input;
        this.bufferStack = bufferStack;
    }

    public void addRule(String tagName, TagRule rule) {
        rules.put(tagName.toLowerCase(), rule);
    }

    public void process() {
        TagTokenizer tokenizer = new TagTokenizer(input);
        tokenizer.start(new TokenHandler() {

            public boolean shouldProcessTag(String name) {
                return rules.containsKey(name.toLowerCase());
            }

            public void tag(Tag tag) {
                TagRule tagRule = (TagRule) rules.get(tag.getName().toLowerCase());
                tagRule.setBufferStack(bufferStack);
                tagRule.process(tag);
            }

            public void text(Text text) {
                text.writeTo(bufferStack.current());
            }

            public void warning(String message, int line, int column) {
                // TODO
                // System.out.println(line + "," + column + ": " + message);
            }
        });
    }
}
