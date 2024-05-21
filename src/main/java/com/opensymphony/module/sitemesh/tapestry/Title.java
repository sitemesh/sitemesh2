package com.opensymphony.module.sitemesh.tapestry;

import org.apache.tapestry.Tapestry;

public abstract class Title extends SiteMeshBase {
    
    public abstract String getDefault();

    public String getTitle() {
        String title = getSiteMeshPage().getTitle();
        return Tapestry.isBlank(title) ? getDefault() : title;
    }
}
