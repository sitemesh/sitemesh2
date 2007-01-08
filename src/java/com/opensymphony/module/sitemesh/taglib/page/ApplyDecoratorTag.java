/*
 * Title:        ApplyDecoratorTag
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.taglib.page;

import com.opensymphony.module.sitemesh.*;
import com.opensymphony.module.sitemesh.filter.PageRequestWrapper;
import com.opensymphony.module.sitemesh.filter.PageResponseWrapper;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;

/**
 * This tag inserts an external resource as a panel into the current Page.
 *
 * <p>The page attribute should point to the panel resource
 * which should expose an entire page (e.g. another JSP file producing
 * HTML). This attribute can be relative to the page it is being called
 * from or an absolute path from the context-root.</p>
 *
 * <p><strong>OR</strong></p>
 *
 * <p>If the page attribute is not specified, the body content is parsed
 * into the {@link com.opensymphony.module.sitemesh.Page} object and has
 * the {@link com.opensymphony.module.sitemesh.Decorator} applied.</p>
 *
 * <p>The (optional) decorator attribute is the name of the
 * {@link com.opensymphony.module.sitemesh.Decorator}
 * to apply to the included page. Note that the implementation of
 * {@link com.opensymphony.module.sitemesh.DecoratorMapper} can overide this.</p>
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision: 1.10 $
 */
public class ApplyDecoratorTag extends BodyTagSupport implements RequestConstants {
    private String page = null;
    private String decorator = null;

    private String contentType = null;
    private String encoding = null;

    private Map params = new HashMap(6);
    private Config config = null;
    private DecoratorMapper decoratorMapper = null;
    private Factory factory;

    /**
     * Tag attribute: URI of page to include.
     * Can be relative to page being called from, or absolute
     * path from context-root of web-app.
     */
    public void setPage(String page) {
        this.page = page;
    }

    /**
     * Add a parameter to the page. This has a package level
     * access modifier so ParamTag can also call it.
     */
    void addParam(String name, String value) {
        params.put(name, value);
    }

    /**
     * Tag attribute: If set, this value will override the 'title'
     * property of the page. This is a convenience utility and is
     * identical to specifing a 'page:param name=title' tag.
     */
    public void setTitle(String title) {
        addParam("title", title);
    }

    /**
     * Tag attribute: If set, this value will override the 'id'
     * property of the page. This is a convenience utility and is
     * identical to specifing a 'page:param name=id' tag.
     */
    public void setId(String id) {
        addParam("id", id);
    }

    /**
     * Tag attribute: Name of Decorator to apply to Page.
     * This is passed to DecoratorMapper to retrieve appropriate
     * Decorator. DecoratorMapper may override if needed.
     *
     * @see com.opensymphony.module.sitemesh.DecoratorMapper
     */
    public void setName(String decorator) {
        if (decorator != null) this.decorator = decorator;
    }

    /** @deprecated Use setName() instead. */
    public void setDecorator(String decorator) {
        setName(decorator);
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public int doStartTag() {
        if (config == null) {
            // set context if not already set
            config = new Config(pageContext.getServletConfig());
            factory = Factory.getInstance(config);
            decoratorMapper = factory.getDecoratorMapper();
        }
        // return page == null ? EVAL_BODY_BUFFERED : SKIP_BODY;
        return EVAL_BODY_BUFFERED;
    }

    /** Ensure that external page contents are included in bodycontent. */
    public int doAfterBody() throws JspException {
        return SKIP_BODY;
    }

    /** Standard taglib method: apply decorator to page. */
    public int doEndTag() throws JspException {
        try {
            // if composite decorator, remember last page
            Page oldPage = (Page) pageContext.getRequest().getAttribute(PAGE);

            // parse bodycontent into Page object
            PageParser parser = getParserSelector().getPageParser(contentType != null ? contentType : "text/html");
            Page pageObj;

            if (page == null) {
                // inline content
                if (bodyContent != null) {
                    pageObj = parser.parse(bodyContent.getString().toCharArray());
                } else {
                    pageObj = parser.parse(new char[]{});
                }
            }
            else if (page.startsWith("http://") || page.startsWith("https://")) {
                try {
                    URL url = new URL(page);
                    URLConnection urlConn = url.openConnection();
                    urlConn.setUseCaches(true);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(urlConn.getInputStream()));

                    StringBuffer sbuf = new StringBuffer();
                    char[] buf = new char[1000];
                    for (; ;) {
                        int moved = in.read(buf);
                        if (moved < 0) break;
                        sbuf.append(buf, 0, moved);
                    }
                    in.close();
                    pageObj = parser.parse(sbuf.toString().toCharArray());
                }
                catch (MalformedURLException e) {
                    throw new JspException(e);
                }
                catch (IOException e) {
                    throw new JspException(e);
                }
            }
            else {
                // external content
                String fullPath = page;
                if (fullPath.length() > 0 && fullPath.charAt(0) != '/') {
                    HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

                    // find absolute path if relative supplied
                    String thisPath = request.getServletPath();

                    // check if it did not return null (could occur when the servlet container
                    // does not use a servlet to serve the requested resouce)
                    if (thisPath == null) {
                        String requestURI = request.getRequestURI();
                        if (request.getPathInfo() != null) {
                            // strip the pathInfo from the requestURI
                            thisPath = requestURI.substring(0, requestURI.indexOf(request.getPathInfo()));
                        }
                        else {
                            thisPath = requestURI;
                        }
                    }

                    fullPath = thisPath.substring(0, thisPath.lastIndexOf('/') + 1) + fullPath;
                    int dotdot;
                    while ((dotdot = fullPath.indexOf("..")) > -1) {
                        int prevSlash = fullPath.lastIndexOf('/', dotdot - 2);
                        fullPath = fullPath.substring(0, prevSlash) + fullPath.substring(dotdot + 2);
                    }
                }

                // include page using filter response
                RequestDispatcher rd = pageContext.getServletContext().getRequestDispatcher(fullPath);
                PageRequestWrapper pageRequest = new PageRequestWrapper((HttpServletRequest) pageContext.getRequest());
                PageResponseWrapper pageResponse = new PageResponseWrapper((HttpServletResponse) pageContext.getResponse(), factory);

                StringBuffer sb = new StringBuffer(contentType != null ? contentType : "text/html");
                if (encoding != null) {
                    sb.append(";charset=").append(encoding);
                }
                pageResponse.setContentType(sb.toString());

                // if rd == null, then the panel was not found, but this isn't correct, so we need to spit out
                // something appropriate. What this is, well...I don't know yet.
                if (rd == null) {
                    throw new ApplyDecoratorException("The specified resource in applyDecorator tag (" + fullPath + ") was not found.");
                }
                rd.include(pageRequest, pageResponse);
                pageObj = pageResponse.getPage();
            }

            // If pageObj == null, then the panel source had some weird error in
            // it. Stop writing bugs like this. They're ugly and they make you smell funny.
            if (pageObj == null) {
                throw new ApplyDecoratorException(page + " did not create a valid page to decorate.");
            }

            // add extra params to Page
            Iterator paramKeys = params.keySet().iterator();
            while (paramKeys.hasNext()) {
                String k = (String) paramKeys.next();
                String v = (String) params.get(k);
                pageObj.addProperty(k, v);
            }

            // get decorator
            if (decorator == null) decorator = "";
            pageObj.setRequest((HttpServletRequest) pageContext.getRequest());
            pageContext.getRequest().setAttribute(DECORATOR, decorator);
            Decorator d = decoratorMapper.getDecorator(((HttpServletRequest) pageContext.getRequest()), pageObj);
            pageContext.getRequest().removeAttribute(DECORATOR);

            // apply decorator
            if (d != null && d.getPage() != null) {
                pageContext.getRequest().setAttribute(PAGE, pageObj);
                pageContext.include(d.getPage());
            }
            else {
                throw new JspException("Cannot locate inline Decorator: " + decorator);
            }

            // clean up
            pageContext.getRequest().setAttribute(PAGE, oldPage);
            params.clear();   // params need to be cleared between invocations - SIM-191
        }
        catch (IOException e) {
            throw new JspException(e);
        }
        catch (ServletException e) {
            throw new JspException(e);
        }
        catch (ApplyDecoratorException e) {
            try {
                pageContext.getOut().println(e.getMessage());
            }
            catch (IOException ioe) {
                System.err.println("IOException thrown in applyDecorator tag: " + e.toString());
            }
        }
        return EVAL_PAGE;
    }

    private PageParserSelector getParserSelector() {
        return Factory.getInstance(config);
    }

    class ApplyDecoratorException extends Exception {
        public ApplyDecoratorException(String s) {
            super(s);
        }
    }
}