/*
 * Title:        Page
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * The Page object wraps the contents of the original (undecorated) page.
 *
 * <p>The original data in its entirity can be written using the writePage()
 * methods. It may also contain a set of properties - these vary among
 * different {@link com.opensymphony.module.sitemesh.PageParser} implementations.</p>
 *
 * <p>Typically a Page is no use to a {@link com.opensymphony.module.sitemesh.Decorator} as it needs
 * specific details relevant to the content-type of that page (<i>e.g.</i> HTML
 * pages). The appropriate {@link com.opensymphony.module.sitemesh.PageParser} is responsible
 * for returning extended implementations of pages such as {@link com.opensymphony.module.sitemesh.HTMLPage}
 * which are of more use to the Decorator. New media types (<i>e.g.</i> WML) could
 * be added to the system by extending Page and implementing an appropriate PageParser.</p>
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.5 $
 */
public interface Page {
    /**
     * Write the entire contents of the <code>Page</code>, in the format before
     * it was parsed, to the <code>Writer</code>.
     *
     * @param out Writer to write to.
     * @exception java.io.IOException Rethrown if cannot write to writer.
     */
    void writePage(Writer out) throws IOException;

    /**
     * Convenience method to return the contents of the <code>Page</code> in its original format.
     *
     * @since 2.1.1
     * @see #writePage(java.io.Writer)
     */
    String getPage();

    /**
     * Write the contents of the <code>&lt;body&gt;</code> tag.
     */
    void writeBody(Writer out) throws IOException;

    /**
     * Convenience method to return the contents of the <code>&lt;body&gt;</code> tag.
     *
     * @since 2.1.1
     * @see #writeBody(java.io.Writer)
     */
    String getBody();

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
     * Get a property embedded into the <code>Page</code> as an <code>int</code>.
     * Returns 0 if property not specified or not valid number.
     *
     * @param name Name of property
     * @return Property value
     */
    int getIntProperty(String name);

    /**
     * Get a property embedded into the <code>Page</code> as a <code>long</code>.
     * Returns 0L if property not specified or not valid number.
     *
     * @param name Name of property
     * @return Property value
     */
    long getLongProperty(String name);

    /**
     * Get a property embedded into the <code>Page</code> as a <code>boolean</code>.
     * Returns true if value starts with '1', 't' or 'y' (case-insensitive) -
     * otherwise returns false.
     *
     * @param name Name of property
     * @return Property value
     */
    boolean getBooleanProperty(String name);

    /**
     * Determine whether a property embedded into the <code>Page</code> has been set.
     *
     * @param name Name of property
     * @return Whether it has been set
     */
    boolean isPropertySet(String name);

    /**
     * Get all available property keys for the <code>Page</code>.
     *
     * @return Property keys
     */
    String[] getPropertyKeys();

    /**
     * Get a <code>Map</code> representing all the properties in the <code>Page</code>.
     *
     * @return Properties map
     */
    Map getProperties();

    /**
     * Return the request of the original page.
     *
     * @deprecated Since Servlet 2.4 API, this is unnecessary - just use the stand HttpServletRequest instance.
     */
    HttpServletRequest getRequest();

    /**
     * Create snapshot of Request. Subsequent modifications to the request by
     * the servlet container will not be returned by {@link #getRequest()}
     */
    void setRequest(HttpServletRequest request);

    /**
     * Manually add a property to page.
     */
    void addProperty(String name, String value);
}