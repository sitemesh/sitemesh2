package com.opensymphony.module.sitemesh.html;

import com.opensymphony.module.sitemesh.SitemeshBufferFragment;

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
     */
    String getContents();

    /**
     * Write out the complete contents of the text block, preserving original formatting.
     */
    void writeTo(SitemeshBufferFragment.Builder buffer, int position);

    /**
     * The position of the text
     */
    int getPosition();

    /**
     * The length of the text
     */
    int getLength();

}
