package com.opensymphony.module.sitemesh.parser.tokenizer;

/**
 * Handler passed to {@link TagTokenizer} that will receive callbacks as 'tags' and 'text' are encountered.
 *
 * @author Joe Walnes
 * @see TagTokenizer
 */
public interface TokenHandler {

    /**
     * Before attempting to parse a tag, the tokenizer will ask the handler whether the tag should be processed - avoiding
     * additional tag parsing makes the tokenizer quicker.
     * <p/>
     * If true is returned, the tokenizer will fully parse the tag and pass it into the {@link #tag(Tag)} method.
     * If false is returned, the tokenizer will not try to parse the tag and pass it to the #{@link #text(Text)} method,
     * untouched.
     */
    boolean shouldProcessTag(String name);

    /**
     * Called when tokenizer encounters an HTML tag (open, close or empty).
     */
    void tag(Tag tag);

    /**
     * Called when tokenizer encounters anything other than a well-formed HTML tag.
     *
     * The Text object is used instead of a String to allow the String to be lazy-loaded.
     */
    void text(Text text);

    /**
     * Called when tokenizer encounters something it cannot correctly parse. Typically the parsing will continue and the
     * unparseable will be treated as a plain text block, however this callback provides indication of this.
     *
     * @param message Error message
     * @param line Line number in input that error occured
     * @param column Column number in input that error occured
     */
    void warning(String message, int line, int column);

}
