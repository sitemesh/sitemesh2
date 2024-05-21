/*
 * Title:        UsePageTag
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.taglib.decorator;

import com.opensymphony.module.sitemesh.taglib.AbstractTag;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

/**
 * Expose the Page as a bean to the page which can then be accessed
 * from scriptlets.
 *
 * <p>Depending on the TEI used, the object will be
 * {@link com.opensymphony.module.sitemesh.Page} or
 * {@link com.opensymphony.module.sitemesh.HTMLPage}.</p>
 *
 * @author <a href="joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.1 $
 *
 * @see UsePageTEI
 * @see UseHTMLPageTEI
 */
public class UsePageTag extends AbstractTag {
    private String id = null;

    /** Set name of variable the Page will be set as. */
    public void setId(String id) {
        this.id = id;
    }

    public final int doEndTag() throws JspException {
        pageContext.setAttribute(id, getPage(), PageContext.PAGE_SCOPE);
        return EVAL_PAGE;
    }
}