package com.opensymphony.module.sitemesh.tapestry;

import org.apache.tapestry.Tapestry;

/**
 * Because Tapestry templating works differently than JSP taglibs,
 * the writeEntireProperty feature is not implemented here.  The built-in
 *
 * &#64;Body component is most frequently used, to do something like
 * this taglib example:
 * <br>
 * <p>
 * <code>
 * &lt;body bgcolor="White"&lt;decorator:getProperty property="body.onload" writeEntireProperty="true" /&gt;&gt;
 * </code>
 * </p>
 * <br>
 * it would be done like this in Tapestry:
 * <p>
 * <code>
 * &lt;body jwcid="&#64;Body" bgcolor="White" onload="ognl:@org.opensymphony.module.sitemesh.tapestry@Util.getProperty('onload', requestCycle)"/&gt;
 * </code>
 * </p>
 * @author Erik Hatcher
 */
public abstract class Property extends SiteMeshBase {

    public abstract String getProperty();

    public abstract String getDefault();

    public String getValue() {
        String propertyName = getProperty();
        String propertyValue = getSiteMeshPage().getProperty(propertyName);
        
        if (Tapestry.isBlank(propertyValue)) {
            propertyValue = getDefault();
        }

        return propertyValue;
    }
}
