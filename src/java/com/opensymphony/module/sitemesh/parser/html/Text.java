package com.opensymphony.module.sitemesh.parser.html;

/**
 * Text returned by HTMLTagTokenizer. Hidden behind interface to allow for lazy-loading.
 *
 * @see TokenHandler
 * @see HTMLTagTokenizer
 *
 * @author Joe Walnes
 */
public interface Text {

    /**
     * Contents of text
     */
    String getText();
    
}
