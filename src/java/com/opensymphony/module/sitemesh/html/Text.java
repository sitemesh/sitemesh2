package com.opensymphony.module.sitemesh.html;

import com.opensymphony.module.sitemesh.html.util.CharArray;

/**
 * Text returned by HTMLTagTokenizer.
 *
 * @see com.opensymphony.module.sitemesh.html.tokenizer.TokenHandler
 * @see com.opensymphony.module.sitemesh.html.tokenizer.TagTokenizer
 *
 * @author Joe Walnes
 */
public interface Text {

    /**
     * Get the complete contents of the text block, preserving original formatting.
     *
     * This has a slight overhead in that it needs to construct a String. For improved performance, use writeTo() instead.
     *
     * @see #writeTo(com.opensymphony.module.sitemesh.html.util.CharArray)
     */
    String getContents();

    /**
     * Write out the complete contents of the text block, preserving original formatting.
     */
    void writeTo(CharArray out);

}
