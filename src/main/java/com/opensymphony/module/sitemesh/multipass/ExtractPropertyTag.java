package com.opensymphony.module.sitemesh.multipass;

import com.opensymphony.module.sitemesh.taglib.decorator.PropertyTag;
import com.opensymphony.module.sitemesh.Page;

public class ExtractPropertyTag extends PropertyTag {

    public int doEndTag() {
        Page page = getPage();
        page.addProperty("_sitemesh.removefrompage." + getProperty(), "true");
        return super.doEndTag();
    }

}
