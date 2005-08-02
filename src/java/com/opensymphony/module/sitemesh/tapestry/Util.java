package com.opensymphony.module.sitemesh.tapestry;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.RequestConstants;
import org.apache.tapestry.IRender;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.valid.RenderString;

/**
 * This utility class gives easy access to the SiteMesh page, with convenience
 * methods for title and property. A common usage would be with OGNL expressions
 * like this:
 * <p/>
 * <html jwcid="@Shell"
 * title="ognl:@com.opensymphony.module.sitemesh.tapestry.Util@getTitle()">
 * <p/>
 * In future versions of Tapestry, thanks to HiveMind integration, this will
 * become a lot cleaner, probably like this:
 * <p/>
 * <html jwcid="@Shell" title="sitemesh:title">
 *
 * @author Erik Hatcher
 */
public class Util {

    public static String getTitle(IRequestCycle cycle) {
        return getPage(cycle).getTitle();
    }

    public static String getProperty(String name, IRequestCycle cycle) {
        return getPage(cycle).getProperty(name);
    }

    public static Page getPage(IRequestCycle cycle) {
        return (Page) cycle.getRequestContext().getRequest().getAttribute(
                RequestConstants.PAGE);
    }

    public static IRender getHeadRenderer(IRequestCycle cycle) {
        return new RenderString(((HTMLPage) getPage(cycle)).getHead(), true);
    }
}