/*
 * Title:        AgentDecoratorMapper
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
 * The AgentDecoratorMapper can determine the user-agent (i.e. web-browser)
 * requesting a page, and map to a suitable Decorator.
 *
 * <p>This can be useful for supplying different versions of the same content
 * for different browsers (e.g. vanilla HTML for Lynx, complex tables and frames
 * for Netscape, extra stuff for IE5, etc).</p>
 *
 * <p>This can also be used to enhance search-engine ratings by using a 'bait and
 * switch' system - this involves showing a search-engine friendly of the content
 * to spiders only.</p>
 *
 * <p>When AgentDecoratorMapper is in the chain, it will request the appropriate Decorator
 * from its parent. It will then add an extention to the filename of the Decorator, and
 * if that file exists it shall be used as the Decorator instead. For example, if the
 * Decorator path is <code>/blah.jsp</code> and the detected user-agent is <code>ie</code>,
 * the path <code>/blah-ie.jsp</code> shall be used.</p>
 *
 * <p>The agent mappings are configured by passing properties with <code>match.</code> as a prefix.
 * For example: 'match.MSIE'=ie , 'match.Lynx'=plain .</p>
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.3 $
 *
 * @see com.opensymphony.module.sitemesh.DecoratorMapper
 */
public class AgentDecoratorMapper extends AbstractDecoratorMapper {
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
            String path = modifyPath(d.getPage(), getExt(request.getHeader("User-Agent")));

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

    /** Get extention for user-agent. */
    private String getExt(String userAgent) {
        Iterator i = map.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            String curr = (String) entry.getKey();
            if (userAgent.indexOf(curr) > -1) return (String) entry.getValue();
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

    /** Initialize user-agent mappings. */
    private void initMap(Properties props) {
        Iterator i = props.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            String key = (String) entry.getKey();
            if (key.startsWith("match.")) {
                String match = key.substring(6);
                String ext = (String) entry.getValue();
                map.put(match, ext);
            }
        }
    }
}