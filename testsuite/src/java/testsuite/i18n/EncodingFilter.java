/*
 * Atlassian Source Code Template.
 * User: sfarquhar
 * Date: 1/08/2002
 * Time: 09:27:01
 * CVS Revision: $Revision: 1.1 $
 * Last CVS Commit: $Date: 2003/11/03 16:27:39 $
 * Author of last CVS Commit: $Author: mbogaert $
 */
package testsuite.i18n;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponseWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author <a href="mailto:scott@atlassian.com">Scott Farquhar</a>
 */
public class EncodingFilter implements Filter
{

    public void destroy()
    {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException
    {
        servletRequest.setCharacterEncoding("UTF-8");
        servletResponse.setContentType("text/html;charset=UTF-8");

        filterChain.doFilter(servletRequest,
                new HttpServletResponseWrapper((HttpServletResponse) servletResponse)
                {
                    public void setContentType(String s)
                    {
                        if (s.length() > "text/html".length() && s.charAt(0) == 't' && s.startsWith("text/html"))
                        {
                            //do nothing.  This call could be trying to set the charset to another charset.
                            //This is the case with Tomcat & Jetty, whose JSP compiler sets the charset, whether it
                            //is specified in the JSP page or not.

                            //NB - this can also be accomplished by setting the charset manually in the JSP page & the decorator,
                            //but this approach allows for run-time flexibility of choosing the charsets.

                            //And besides, this is the way that we do it in JIRA, and I want to test this against different
                            //servers
                        }
                        else
                        {
                            super.setContentType(s);
                        }
                    }

                });
    }

    public void init(FilterConfig filterConfig) throws ServletException
    {
    }

}
