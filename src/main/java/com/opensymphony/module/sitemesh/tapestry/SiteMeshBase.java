package com.opensymphony.module.sitemesh.tapestry;

import com.opensymphony.module.sitemesh.Page;
import org.apache.tapestry.BaseComponent;

/**
 * Base class for Tapestry decorator components.
 *
 * @author Erik Hatcher
 */
public class SiteMeshBase extends BaseComponent {
    public Page getSiteMeshPage() {
        return Util.getPage(getPage().getRequestCycle());
    }
}
