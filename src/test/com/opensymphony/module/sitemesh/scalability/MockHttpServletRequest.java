package com.opensymphony.module.sitemesh.scalability;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.*;

/**
*/
public class MockHttpServletRequest implements HttpServletRequest
{
    private Map parameterMap = new LinkedHashMap();
    private String requestURL = "http://mockservletrequest";
    private String servletPath;
    private String protocol;
    private String method;
    private String remoteAddr;
    private String contextPath;
    private String characterEncoding;
    private Map headerMap = new HashMap();
    private Map attributeMap = new HashMap();
    private final ServletInputStream servletInputStream;
    private final BufferedReader bufferedReader;
    private HttpSession httpSession;
    private Cookie[] cookies = new Cookie[0];
    private Map<String, String> cookieMap;

    public MockHttpServletRequest()
    {
        this((BufferedReader) null);
    }

    public MockHttpServletRequest(ServletInputStream servletInputStream)
    {
        this.servletInputStream = servletInputStream;
        bufferedReader = null;
    }

    public MockHttpServletRequest(BufferedReader bufferedReader)
    {
        this.bufferedReader = bufferedReader;
        servletInputStream = null;
    }

    /**
     * Use this constructor if you want to mock out the session returned from the request
     *
     */
    public MockHttpServletRequest(final HttpSession httpSession)
    {
        this.servletInputStream = null;
        this.bufferedReader = null;
        this.httpSession = httpSession;
    }

    public String getAuthType()
    {
        return null;
    }

    public Cookie[] getCookies()
    {
        return cookies;
    }

    public void setCookies(Cookie[] cookies)
    {
        this.cookies = cookies;
    }

    /**
     * Converts the key valeus of a map to an array of cookies
     *
     * @param cookieMap a map of cookies
     * @return an array of cookies or null if its empty or null
     */
    public Cookie[] toCookies(Map<String, String> cookieMap)
    {
        if (cookieMap == null || cookieMap.isEmpty())
        {
            return null;
        }
        Cookie[] cookies = new Cookie[cookieMap.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : cookieMap.entrySet())
        {
            cookies[i++] = new Cookie(entry.getKey(), entry.getValue());
        }
        return cookies;
    }


    public long getDateHeader(String string)
    {
        return 0;
    }

    public String getHeader(String key)
    {
        return (String) headerMap.get(key);
    }

    public void setHeader(String key, String value)
    {
        headerMap.put(key, value);
    }

    public Enumeration getHeaders(String string)
    {
        Hashtable hashtable = new Hashtable(headerMap);
        return hashtable.elements();
    }

    public Enumeration getHeaderNames()
    {
        Hashtable hashtable = new Hashtable(headerMap);
        return hashtable.keys();
    }

    public int getIntHeader(String string)
    {
        return 0;
    }

    public String getMethod()
    {
        return method;
    }

    public void setMethod(final String method)
    {
        this.method = method;
    }

    public String getPathInfo()
    {
        return null;
    }

    public String getPathTranslated()
    {
        return null;
    }

    public String getContextPath()
    {
        return contextPath;
    }

    public void setContextPath(final String contextPath)
    {
        this.contextPath = contextPath;
    }

    public String getQueryString()
    {
        StringBuilder queryString = new StringBuilder();
        for (Iterator iterator = parameterMap.entrySet().iterator(); iterator.hasNext();)
        {
            Map.Entry entry = (Map.Entry) iterator.next();
            queryString.append(entry.getKey()).
                    append("=").
                    //TODO: Should probably make this work for multiple values
                            append(((String[]) entry.getValue())[0]);
            if (iterator.hasNext())
            {
                queryString.append("&");
            }
        }
        return queryString.toString();
    }

    public String getRemoteUser()
    {
        return null;
    }

    public boolean isUserInRole(String string)
    {
        return false;
    }

    public Principal getUserPrincipal()
    {
        return null;
    }

    public String getRequestedSessionId()
    {
        return null;
    }

    public String getRequestURI()
    {
        return requestURL;
    }

    public StringBuffer getRequestURL()
    {
        return new StringBuffer(requestURL);
    }

    public String getServletPath()
    {
        return servletPath;
    }

    public void setServletPath(String servletPath)
    {
        this.servletPath = servletPath;
    }

    public HttpSession getSession(boolean b)
    {
        return httpSession;
    }

    public HttpSession getSession()
    {
        return httpSession;
    }

    public boolean isRequestedSessionIdValid()
    {
        return false;
    }

    public boolean isRequestedSessionIdFromCookie()
    {
        return false;
    }

    public boolean isRequestedSessionIdFromURL()
    {
        return false;
    }

    public boolean isRequestedSessionIdFromUrl()
    {
        return false;
    }

    public Object getAttribute(String key)
    {
        return attributeMap.get(key);
    }

    public Enumeration getAttributeNames()
    {
        return null;
    }

    public String getCharacterEncoding()
    {
        return characterEncoding;
    }

    public void setCharacterEncoding(String encoding) throws UnsupportedEncodingException
    {
        Charset.forName(encoding);
        this.characterEncoding = encoding;
    }

    public int getContentLength()
    {
        return 0;
    }

    public String getContentType()
    {
        return null;
    }

    public ServletInputStream getInputStream() throws IOException
    {
        return servletInputStream;
    }

    public String getParameter(String name)
    {
        String[] values = getParameterValues(name);
        if (values == null || values.length == 0)
        {
            return null;
        }
        return values[0];
    }

    public void setParameter(String name, String value)
    {
        if (value == null)
        {
            parameterMap.remove(name);
        }
        else
        {
            // Put the value into our Map of String Arrays.
            parameterMap.put(name, new String[] { value });
        }
    }

    public Enumeration getParameterNames()
    {

        return new Hashtable(parameterMap).keys();
    }

    public String[] getParameterValues(String name)
    {
        return (String[]) parameterMap.get(name);
    }

    public Map getParameterMap()
    {
        return Collections.unmodifiableMap(parameterMap);
    }

    public String getProtocol()
    {
        return protocol;
    }

    public String getScheme()
    {
        return null;
    }

    public String getServerName()
    {
        return null;
    }

    public int getServerPort()
    {
        return 0;
    }

    public BufferedReader getReader() throws IOException
    {
        return bufferedReader;
    }

    public String getRemoteAddr()
    {
        return remoteAddr;
    }

    public void setRemoteAddr(final String remoteAddr)
    {
        this.remoteAddr = remoteAddr;
    }

    public String getRemoteHost()
    {
        return null;
    }

    public void setAttribute(String key, Object value)
    {
        if (value != null)
        {
            attributeMap.put(key, value);
        }
        else
        {
            attributeMap.remove(key);
        }
    }

    public void removeAttribute(String string)
    {
    }

    public Locale getLocale()
    {
        return null;
    }

    public Enumeration getLocales()
    {
        return null;
    }

    public boolean isSecure()
    {
        return false;
    }

    public RequestDispatcher getRequestDispatcher(String string)
    {
        return null;
    }

    public String getRealPath(String string)
    {
        return null;
    }

    public int getRemotePort()
    {
        return 0;
    }

    public String getLocalName()
    {
        return null;
    }

    public String getLocalAddr()
    {
        return null;
    }

    public int getLocalPort()
    {
        return 0;
    }

    public void setRequestURL(String requestURL)
    {
        this.requestURL = requestURL;
    }

    public void setProtocol(final String protocol)
    {
        this.protocol = protocol;
    }
}