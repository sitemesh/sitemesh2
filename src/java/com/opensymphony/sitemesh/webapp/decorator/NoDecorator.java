package com.opensymphony.sitemesh.webapp.decorator;

import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * If no decorator is to be applied to a page, this will ensure the original content gets written out.
 *
 * @author Joe Walnes
 * @since SiteMesh 3.0
 */
public class NoDecorator extends BaseWebAppDecorator {

    protected void render(Content content, HttpServletRequest request, HttpServletResponse response,
                          ServletContext servletContext, SiteMeshWebAppContext webAppContext)
            throws IOException, ServletException {

        response.setContentLength(content.originalLength());

        if (webAppContext.isUsingStream()) {
            PrintWriter writer = new PrintWriter(response.getOutputStream());
            content.writeOriginal(writer);
            writer.flush(); //flush writer to underlying outputStream
            response.getOutputStream().flush();
        } else {
            PrintWriter writer = response.getWriter();
            content.writeOriginal(writer);
            response.getWriter().flush();
        }
    }

}
