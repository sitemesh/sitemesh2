/*
 * Title:        PropertyTag
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.taglib.decorator;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.taglib.AbstractTag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

/**
 * Write property of Page to out.
 *
 * @author <a href="joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.1 $
 *
 * @see com.opensymphony.module.sitemesh.Page#getProperty(java.lang.String)
 */
public class PropertyTag extends AbstractTag {
    private String propertyName, defaultValue;
    private boolean writeEntireProperty = false;

    /** Key of property to write. */
    public void setProperty(String propertyName) {
        this.propertyName = propertyName;
    }

    /** Value to write if no property matching key is found (optional). */
    public void setDefault(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /** When begins with y, t or 1, the full attribute (name + value) is written. */
    public final void setWriteEntireProperty(String writeEntireProperty) {
        if (writeEntireProperty == null || writeEntireProperty.trim().length() == 0)
            return;

        switch (writeEntireProperty.charAt(0)) {
            case '1':
            case 't':
            case 'T':
            case 'y':
            case 'Y':
                this.writeEntireProperty = true;
                break;
            default:
                this.writeEntireProperty = false;
        }
    }

    public final int doEndTag() throws JspException {
        try {
            HTMLPage htmlPage = (HTMLPage)getPage();
            String propertyValue = htmlPage.getProperty(propertyName);

            if (propertyValue == null || propertyValue.trim().length() == 0)
                propertyValue = defaultValue;

            if (propertyValue != null) {
                JspWriter out = pageContext.getOut();
                if (writeEntireProperty) {
                    out.print(" ");
                    out.print(propertyName.substring(propertyName.lastIndexOf('.') + 1));
                    out.print("=\"");
                    out.print(propertyValue);
                    out.print("\"");
                }
                else {
                    out.print(propertyValue);
                }
            }
        }
        catch (Exception e) {
            trace(e);
        }
        return EVAL_PAGE;
    }

    public void release() {
        defaultValue = null;
        propertyName = null;
        super.release();
    }
}