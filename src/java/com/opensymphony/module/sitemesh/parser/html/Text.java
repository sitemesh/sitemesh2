package com.opensymphony.module.sitemesh.parser.html;

import com.opensymphony.module.sitemesh.util.CharArray;

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

    void writeTo(CharArray out);
}
