/*
 * Title:        HTMLPage
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh;

import java.io.IOException;
import java.io.Writer;

/**
 * Extension of {@link com.opensymphony.module.sitemesh.Page} providing access to HTML data.
 *
 * <p>The page is parsed and the <code>&lt;title&gt;</code>, <code>&lt;head&gt;</code>
 * (minus the <code>&lt;title&gt;</code>) and <code>&lt;body&gt;</code> are split
 * into chunks. These can then be used by a {@link com.opensymphony.module.sitemesh.Decorator}.
 * Properties are also extracted from the HTML.</p>
 *
 * <h2>Page Properties</h2>
 *
 * <p>When the page is parsed, values from certain tags are added to the properties
 * to allow easy access to them. The following tags have properties extracted from them.</p>
 *
 * <ul>
 *   <li>
 *     <b>HTML Tag</b><br>
 *     All attributes of the <code>&lt;html&gt;</code>
 *     tag shall be added as properties.
 *   </li>
 *   <li>
 *     <b>TITLE Tag</b><br>
 *     The contents of the <code>&lt;title&gt;</code> tag
 *     shall be added as the <code>title</code> property.
 *   </li>
 *   <li>
 *     <b>META Tags</b><br>
 *     All the <code>&lt;meta&gt;</code> tags with
 *     <code>name</code> and <code>content</code> attributes
 *     will be added with the <code>meta</code> prefix.
 *   </li>
 *   <li>
 *     <b>BODY Tag</b><br>
 *     All attributes of the <code>&lt;body&gt;</code> tag
 *     shall be added as properties with the
 *     <code>body</code> prefix.
 *   </li>
 * </ul>
 * <h4>Example</h4>
 * <pre>
 *   <xmp>
 *     <html template="funky">
 *       <head>
 *         <title>My Funky Page</title>
 *         <meta name="description" content="Description of my page.">
 *         <meta name="author" content="Bob">
 *         ...
 *       </head>
 *       <body text="#ff00ff" bgcolor="green">
 *         ...
 *       </body>
 *     </html>
 *   </xmp>
 * template=funky
 * title=My Funky Page
 * meta.description=Description of my page.
 * meta.author=Bob
 * body.text=#ff00ff
 * body.bgcolor=green
 * </pre>
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.3 $
 */
public interface HTMLPage extends Page {

    /**
     * Write the contents of the <code>&lt;head&gt;</code> tag.
     */
    void writeHead(Writer out) throws IOException;

    /**
     * Convenience method to return the contents of the <code>&lt;head&gt;</code> tag as a String.
     *
     * @since 2.1.1
     * @see #writeHead(java.io.Writer) 
     */
    String getHead();

    /**
     * Check to see if this page contains an
     * <a href="http://www.w3.org/TR/html4/present/frames.html">HTML frameset</a>.
     */
    boolean isFrameSet();

    /**
     * Marks this page as a frameset.
     *
     * @since 2.3
     * @see #isFrameSet()
     */
    void setFrameSet(boolean frameset);
    
}