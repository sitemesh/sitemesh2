/*
 * Title:        LanguageDecoratorMapper
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.mapper;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.DecoratorMapper;
import com.opensymphony.module.sitemesh.Page;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * The LanguageDecoratorMapper can determine the preferred language set in the
 * browser requesting a page, and map to a suitable Decorator (using the
 * "Accept-Language" HTTP header).
 *
 * <p>This can be useful for supplying different versions of the same content
 * for different languages.</p>
 *
 * <p>When LanguageDecoratorMapper is in the chain, it will request the appropriate Decorator
 * from its parent. It will then add an extention to the filename of the Decorator, and
 * if that file exists it shall be used as the Decorator instead. For example, if the
 * Decorator path is <code>/blah.jsp</code> and the detected preferred language is <code>en</code>,
 * the path <code>/blah-en.jsp</code> shall be used.</p>
 *
 * <p>The language mappings are configured by passing properties with <code>match.</code> as a prefix.
 * For example: 'match.en'=engl , 'match.nl'=dutch .</p>
 *
 * @author <a href="mailto:pathos@pandora.be">Mathias Bogaert</a>
 *
 * @see com.opensymphony.module.sitemesh.DecoratorMapper
 */
public class LanguageDecoratorMapper extends AbstractDecoratorMapper {
    private Map map = null;

    public void init(Config config, Properties properties, DecoratorMapper parent) throws InstantiationException {
        super.init(config, properties, parent);
        map = new HashMap();
        initMap(properties);
    }

    public Decorator getDecorator(HttpServletRequest request, Page page) {
        try {
            Decorator result = null;
            final Decorator d = super.getDecorator(request, page);
            String path = modifyPath(d.getPage(), getExt(request.getHeader("Accept-Language")));

            File decFile = new File(config.getServletContext().getRealPath(path));

            if (decFile.isFile()) {
                result = new DefaultDecorator(d.getName(), path, null) {
                    public String getInitParameter(String paramName) {
                        return d.getInitParameter(paramName);
                    }
                };
            }
            return result == null ? super.getDecorator(request, page) : result;
        }
        catch (NullPointerException e) {
            return super.getDecorator(request, page);
        }
    }

    /** Get extention for the language. */
    private String getExt(String acceptLanguage) {
        Iterator i = map.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();

           // Get the first language (preferred one) in the header, and
            // only check the first two chars (the acceptLanguage could be en-gb, but
            // we don't support this for now).
            if (acceptLanguage.substring(0, 2).equals(entry.getKey())) {
                return (String) entry.getValue();
            }

            // When the user-agent has multiple accept-languages (separated by ;),
            // these are ignored because the site creator may wish that if the
            // preferred language is not supported, the page uses the default
            // decorator (in the default language), and not in some other
            // language given by the browser (most of them are specified by
            // default at install).
        }
        return null;
    }

    /** Change /abc/def.jsp into /abc/def-XYZ.jsp */
    private static String modifyPath(String path, String ext) {
        int dot = path.indexOf('.');
        if (dot > -1) {
            return path.substring(0, dot) + '-' + ext + path.substring(dot);
        }
        else {
            return path + '-' + ext;
        }
    }

    /** Initialize language mappings. */
    private void initMap(Properties props) {
        Iterator i = props.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            String key = (String) entry.getKey();
            if (key.startsWith("match.")) {
                String match = key.substring(6);
               map.put(match, entry.getValue());
            }
        }
    }
}