package com.opensymphony.module.sitemesh.parser.html;

import com.opensymphony.module.sitemesh.util.CharArray;

import java.io.CharArrayReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Splits a chunk of HTML into 'text' and 'tag' tokens, for easy processing. Is VERY tolerant to badly formed HTML.
 * <p/>
 * <h3>Usage</h3>
 * <p/>
 * You need to supply a custom {@link TokenHandler}.
 * <p/>
 * <pre>char[] input = ...;
 * HTMLTagTokenizer tokenizer = new HTMLTagTokenizer(input);
 * TokenHandler handler = new MyTokenHandler();
 * tokenizer.start(handler);</pre>
 *
 * @author Joe Walnes
 * @see TokenHandler
 * @see HTMLPageParser
 */
public class HTMLTagTokenizer implements Tag, Text {

    private final char[] input;

    private TokenHandler handler;

    private int position;
    private int length;

    private String name;
    private int type;
    private final List attributes = new ArrayList();

    public HTMLTagTokenizer(char[] input) {
        this.input = input;
    }

    public HTMLTagTokenizer(String input) {
        this(input.toCharArray());
    }

    public void start(TokenHandler handler) {
        this.handler = handler;
        Parser parser = new Parser(this, new CharArrayReader(input));
        parser.start();
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public String getText() {
        return new String(input, position, length);
    }

    public void writeTo(CharArray out) {
        out.append(input, position, length);
    }

    public int getAttributeCount() {
        return attributes == null ? 0 : attributes.size() / 2;
    }

    public String getAttributeName(int index) {
        return (String) attributes.get(index * 2);
    }

    public String getAttributeValue(int index) {
        return (String) attributes.get(index * 2 + 1);
    }

    public String getAttributeValue(String name) {
        // todo: optimize
        if (attributes == null) {
            return null;
        }
        final int len = attributes.size();
        for (int i = 0; i < len; i+=2) {
            if (name.equalsIgnoreCase((String) attributes.get(i))) {
                return (String) attributes.get(i + 1);
            }
        }
        return null;
    }

    public boolean hasAttribute(String name) {
        return getAttributeValue(name) != null;
    }

    public void parsedText(int position, int length) {
        this.position = position;
        this.length = length;
        handler.text((Text) this);
    }

    public void parsedTag(int type, String name, int start, int length) {
        this.type = type;
        this.name = name;
        this.position = start;
        this.length = length;
        handler.tag((Tag) this);
        attributes.clear();
    }

    public void parsedAttribute(String name, String value, boolean quoted) {
        attributes.add(name);
        if (quoted) {
            attributes.add(value.substring(1, value.length() - 1));
        } else {
            attributes.add(value);
        }
    }

    public void error(String message, int line, int column) {
        handler.error(message, line, column);
    }

    public boolean caresAboutTag(String name) {
        return handler.caresAboutTag(name);
    }
}
