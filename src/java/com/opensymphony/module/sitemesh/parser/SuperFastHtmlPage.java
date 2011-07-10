package com.opensymphony.module.sitemesh.parser;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.SitemeshBuffer;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class SuperFastHtmlPage extends SuperFastPage implements HTMLPage
{
    private final char[] head;

    public SuperFastHtmlPage(SitemeshBuffer sitemeshBuffer, int bodyStart, int bodyLength, Map<String, String> bodyProperties)
    {
        this(sitemeshBuffer, bodyStart, bodyLength, bodyProperties, null, null, null, null);
    }

    /**
     *
     * @param sitemeshBuffer The buffer for the page
     * @param bodyStart The start of the body
     * @param bodyLength The length of the body
     * @param bodyProperties The properties of the body
     * @param head The head section
     * @param title The title
     * @param metaAttributes The meta attributes found in the head section
     * @param pageProperties The page properties extracted from the head section
     */
    public SuperFastHtmlPage(SitemeshBuffer sitemeshBuffer, int bodyStart, int bodyLength, Map<String, String> bodyProperties,
            char[] head, String title, Map<String, String> metaAttributes, Map<String, String> pageProperties)
    {
        super(sitemeshBuffer, bodyStart, bodyLength);
        this.head = head;
        if (title == null)
        {
            title = "";
        }
        addProperty("title", title);
        addProperties(metaAttributes, "meta.");
        addProperties(bodyProperties, "body.");
        addProperties(pageProperties, "page.");
    }

    private void addProperties(Map<String, String> properties, String prefix)
    {
        if (properties != null)
        {
            for (Map.Entry<String, String> property : properties.entrySet())
            {
                addProperty(prefix + property.getKey(), property.getValue());
            }
        }
    }

    public void writeHead(Writer out) throws IOException
    {
        if (head != null)
        {
            out.write(head);
        }
    }

    public String getHead()
    {
        if (head != null)
        {
            return new String(head);
        }
        else
        {
            return "";
        }
    }

    public boolean isFrameSet()
    {
        return false;
    }

    public void setFrameSet(boolean frameset)
    {
        throw new UnsupportedOperationException();
    }
}
