package com.opensymphony.module.sitemesh.parser.html;

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

    private int currentType;
    private int currentStart;
    private int currentEnd;
    private String currentName;
    private String currentText;
    private List currentAttributes = new ArrayList();

    public HTMLTagTokenizer(char[] input) {
        this.input = input;
    }

    public HTMLTagTokenizer(String input) {
        this(input.toCharArray());
    }

    public synchronized void start(TokenHandler handler) {
        this.handler = handler;
        Parser parser = new Parser(this, new CharArrayReader(input));
        parser.yyparse();
    }

    public String getCompleteTag() {
        return new String(input, currentStart, currentEnd - currentStart);
    }

    public String getName() {
        return currentName;
    }

    public int getType() {
        return currentType;
    }

    public String getText() {
        return currentText;
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

    public void parsedTag(int type, String name, int start, int end) {
        this.currentType = type;
        this.currentName = name;
        this.currentStart = start;
        this.currentEnd = end;
        handler.tag((Tag) this);
        this.currentAttributes = null;
        this.currentName = null;
        this.currentType = Tag.UNKNOWN;
        this.currentStart = 0;
        this.currentEnd = 0;
    }

    public void parsedText(String text) {
        this.currentText = text;
        handler.text((Text) this);
        this.currentText = null;
    }

    public void parsedText(int start, int end) {
        this.currentText = new String(input, start, end - start);
        handler.text((Text) this);
        this.currentText = null;
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
