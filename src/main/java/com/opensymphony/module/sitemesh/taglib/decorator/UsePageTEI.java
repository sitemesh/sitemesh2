/*
 * Title:        UsePageTEI
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.taglib.decorator;

import jakarta.servlet.jsp.tagext.TagData;
import jakarta.servlet.jsp.tagext.TagExtraInfo;
import jakarta.servlet.jsp.tagext.VariableInfo;

/**
 * TagExtraInfo implementation to expose Page object as variable.
 *
 * @author <a href="joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.1 $
 *
 * @see UsePageTag
 * @see UseHTMLPageTEI
 */
public class UsePageTEI extends TagExtraInfo {
    protected String getType() {
        return "com.opensymphony.module.sitemesh.Page";
    }

    public VariableInfo[] getVariableInfo(TagData data) {
        String id = data.getAttributeString("id");
        return new VariableInfo[] {new VariableInfo(id, getType(), true, VariableInfo.AT_END)};
    }
}