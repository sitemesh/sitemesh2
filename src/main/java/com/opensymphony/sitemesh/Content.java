package com.opensymphony.sitemesh;

import java.io.IOException;
import java.io.Writer;

/**
 * @author Joe Walnes
 * @since SiteMesh 3
 */
public interface Content {

    /**
     * Write out the original unprocessed content.
     */
    void writeOriginal(Writer writer) throws IOException;

    /**
     * Length of the original unprocessed content.
     */
    int originalLength();


    /**
     * Write the contents of the <code>&lt;body&gt;</code> tag.
     */
    void writeBody(Writer out) throws IOException;

    /**
     * Write the contents of the <code>&lt;head&gt;</code> tag.
     */
    void writeHead(Writer out) throws IOException;

    /**
     * Get the Title of the document
     */
    String getTitle();


    /**
     * Get a property embedded into the <code>Page</code> as a <code>String</code>.
     *
     * @param name Name of property
     * @return Property value
     */
    String getProperty(String name);


    /**
     * Get all available property keys for the <code>Page</code>.
     *
     * @return Property keys
     */
    String[] getPropertyKeys();

    /**
     * Manually add a property to page.
     */
    void addProperty(String name, String value);

}
