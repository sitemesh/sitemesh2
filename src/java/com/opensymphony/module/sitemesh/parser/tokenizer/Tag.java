package com.opensymphony.module.sitemesh.parser.tokenizer;

import com.opensymphony.module.sitemesh.util.CharArray;

/**
 * Tag returned by HTMLTagTokenizer. Allows easy access to element name and attributes.
 *
 * @see TokenHandler
 * @see TagTokenizer
 *
 * @author Joe Walnes
 */
public interface Tag {

    int OPEN = 1;
    int CLOSE = 2;
    int EMPTY = 3;

    /**
     * Get the complete tag in its original form, preserving original formatting.
     *
     * This has a slight overhead in that it needs to construct a String. For improved performance, use writeTo() instead.
     *
     * @see #writeTo(com.opensymphony.module.sitemesh.util.CharArray)
     */
    String getContents();

    /**
     * Write out the complete tag in its original form, preserving original formatting.
     */
    void writeTo(CharArray out);

    /**
     * Name of tag (ie. element name).
     */
    String getName();

    /**
     * Type of tag: <br/>
     * &lt;blah&gt; - Type.OPEN<br/>
     * &lt;/blah&gt; - Type.CLOSE<br/>
     * &lt;blah/&gt; - Type.EMPTY<br/>
     */
    int getType();

    int getAttributeCount();
    String getAttributeName(int index);
    String getAttributeValue(int index);
    String getAttributeValue(String name);
    boolean hasAttribute(String name);

}
