package com.opensymphony.sitemesh.webapp.decorator;

import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
        
        PrintWriter writer;
        if (webAppContext.isUsingStream()) {
            writer = new PrintWriter(response.getOutputStream());
        } else {
            writer = response.getWriter();
        }
        content.writeOriginal(writer);
        writer.flush();
    }

}
