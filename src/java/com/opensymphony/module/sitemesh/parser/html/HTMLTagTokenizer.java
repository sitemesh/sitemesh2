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
    private List currentAttributes = new ArrayList();

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
        return currentAttributes == null ? 0 : currentAttributes.size();
    }

    public String getAttributeName(int index) {
        return ((Attribute) currentAttributes.get(index)).name;
    }

    public String getAttributeValue(int index) {
        return ((Attribute) currentAttributes.get(index)).value;
    }

    public String getAttributeValue(String name) {
        // todo: optimize
        for (int i = 0; i < getAttributeCount(); i++) {
            if (getAttributeName(i).equalsIgnoreCase(name)) {
                return getAttributeValue(i);
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
        currentAttributes = null;
    }

    public void parsedAttribute(String name, String value, boolean quoted) {
        // TODO: optimize this... most attributes are ignored, so only bother initializing a heavy Map when
        // absolutely positively necessary.
        if (currentAttributes == null) {
            currentAttributes = new ArrayList();
        }
        Attribute attribute = new Attribute();
        attribute.name = name;
        if (quoted) {
            attribute.value = value.substring(1, value.length() - 1);
        } else {
            attribute.value = value;
        }
        currentAttributes.add(attribute);
    }

    public void error(String message, int line, int column) {
        handler.error(message, line, column);
    }

    private static class Attribute {
        String name;
        String value;
    }
}
