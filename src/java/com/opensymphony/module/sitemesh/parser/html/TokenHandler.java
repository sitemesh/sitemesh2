package com.opensymphony.module.sitemesh.parser.html;

/**
 *
 * @author Joe Walnes
 */
public interface TokenHandler {

    boolean caresAboutTag(String name);
    
    /**
     * Called when tokenizer encounters an HTML tag (open, close or empty).
     */
    void tag(Tag tag);

    /**
     * Called when tokenizer encounters anything other than a well-formed HTML tag.
     */
    void text(Text text);

    /**
     * Called when tokenizer encounters something it cannot correctly parse.
     *
     * @param message Error message
     * @param line Line in input that error occured
     * @param column Column in input that error occured
     */
    void error(String message, int line, int column);

}
