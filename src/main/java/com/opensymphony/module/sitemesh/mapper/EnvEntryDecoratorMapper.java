/*
 * Title:        EnvEntryDecoratorMapper
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.mapper;

import com.opensymphony.module.sitemesh.Decorator;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The EnvEntryDecoratorMapper allows the reference to a web-app environment entry for the
 * decorator name, and falls back to ConfigDecoratorMapper's behavior if no matching
 * environment entry is found.
 *
 * <p>In some cases, it's desirable to allow a deployer, as opposed to a developer,
 * to specify a decorator. In a .WAR file, this can be very difficult, since
 * decorator mappings are specified in <code>decorators.xml</code> (more or less).
 * This mapper corrects that by allowing two types of mapping. If the decorator
 * name is found in an <code>&lt;env-entry&gt;</code>, the entry's value is used
 * as the decorator reference.</p>
 *
 * <p>Known Issues:</p>
 * <ol>
 *   <li>It still uses the decorator path (from <code>decorators.xml</code>). This
 *   needs to be corrected for full functionality. If anyone has a suggestion
 *   on how...</li>
 * </ol>
 *
 * @author <a href="mailto:joeo@enigmastation.com">Joseph B. Ottinger</a>
 * @version $Revision: 1.3 $
 *
 * @see com.opensymphony.module.sitemesh.mapper.ConfigDecoratorMapper
 */
public class EnvEntryDecoratorMapper extends ConfigDecoratorMapper {
    /**
     * Retrieves the {@link com.opensymphony.module.sitemesh.Decorator}
     * specified by the decorator name. If it's not in the environment
     * entries of the web application, assume it's a named decorator
     * from <code>decorators.xml</code>.
     */
    public Decorator getNamedDecorator(HttpServletRequest request, String name) {
        String resourceValue = getStringResource(name);
        if (resourceValue == null) {
            return super.getNamedDecorator(request, name);
        }
        else {
            return new DefaultDecorator(name, resourceValue, null);
        }
    }

    /**
     * This pulls a value out of the web-app environment.
     * If the value isn't there, returns null.
     */
    public static String getStringResource(String name) {
        String value = null;
        Context ctx = null;
        try {
            ctx = new InitialContext();
            Object o = ctx.lookup("java:comp/env/" + name);
            if (o != null) {
                value = o.toString();
            }
        }
        catch (NamingException ne) {
        }
        finally {
            try {
                if (ctx != null) ctx.close();
            }
            catch (NamingException ne) {
            }
        }
        return value;
    }
}