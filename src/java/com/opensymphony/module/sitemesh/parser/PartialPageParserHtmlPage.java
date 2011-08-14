package com.opensymphony.module.sitemesh.parser;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public class PartialPageParserHtmlPage extends PartialPageParserPage implements HTMLPage
{
    private final SitemeshBufferFragment head;

    public PartialPageParserHtmlPage(SitemeshBuffer sitemeshBuffer, SitemeshBufferFragment body, Map<String, String> bodyProperties)
    {
        this(sitemeshBuffer, body, bodyProperties, null, null, null, null);
    }

    /**
     *
     * @param sitemeshBuffer The buffer for the page
     * @param body The body fragment
     * @param bodyProperties The properties of the body
     * @param head The head section
     * @param title The title
     * @param metaAttributes The meta attributes found in the head section
     * @param pageProperties The page properties extracted from the head section
     */
    public PartialPageParserHtmlPage(SitemeshBuffer sitemeshBuffer, SitemeshBufferFragment body, Map<String, String> bodyProperties,
                                     SitemeshBufferFragment head, String title, Map<String, String> metaAttributes, Map<String, String> pageProperties)
    {
        super(sitemeshBuffer, body);
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
            head.writeTo(out);
        }
    }

    public String getHead()
    {
        if (head != null)
        {
            StringWriter headString = new StringWriter();
            try {
                head.writeTo(headString);
            } catch (IOException e) {
                throw new RuntimeException("IOException occured while writing to buffer?", e);
            }
            return headString.toString();
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
