package com.opensymphony.module.sitemesh.html.tokenizer;

/**
 * Splits a chunk of HTML into 'text' and 'tag' tokens, for easy processing. Is VERY tolerant to badly formed HTML.
 * <p/>
 * <h3>Usage</h3>
 * <p/>
 * You need to supply a custom {@link TokenHandler} that will receive callbacks as text and tags are processed.
 * <p/>
 * <pre>char[] input = ...;
 * HTMLTagTokenizer tokenizer = new HTMLTagTokenizer(input);
 * TokenHandler handler = new MyTokenHandler();
 * tokenizer.start(handler);</pre>
 *
 * @author Joe Walnes
 * @see TokenHandler
 * @see com.opensymphony.module.sitemesh.parser.HTMLPageParser
 */
public class TagTokenizer {

    private final char[] input;

    public TagTokenizer(char[] input) {
        this.input = input;
    }

    public TagTokenizer(String input) {
        this(input.toCharArray());
    }

    public void start(TokenHandler handler) {
        Parser parser = new Parser(input, handler);
        parser.start();
    }

}
