package com.opensymphony.module.sitemesh.parser;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.SitemeshWriter;
import com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorage;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public class PartialPageParserHtmlPage extends AbstractPage implements HTMLPage
{
    private final SitemeshBufferFragment head;
    private final SitemeshBufferFragment body;
    private final SitemeshBuffer sitemeshBuffer;

    public PartialPageParserHtmlPage(SitemeshBuffer sitemeshBuffer)
    {
        this(sitemeshBuffer, null, null, null, null, null, null);
    }

    public PartialPageParserHtmlPage(SitemeshBuffer sitemeshBuffer, SitemeshBufferFragment body, Map<String, String> bodyProperties)
    {
        this(sitemeshBuffer, body, bodyProperties, null, null, null, null);
    }

    /**
     * @param sitemeshBuffer The buffer for the page
     * @param body           The body fragment
     * @param bodyProperties The properties of the body
     * @param head           The head section
     * @param title          The title
     * @param metaAttributes The meta attributes found in the head section
     * @param pageProperties The page properties extracted from the head section
     */
    public PartialPageParserHtmlPage(SitemeshBuffer sitemeshBuffer, SitemeshBufferFragment body, Map<String, String> bodyProperties,
                                     SitemeshBufferFragment head, String title, Map<String, String> metaAttributes, Map<String, String> pageProperties)
    {
        super(sitemeshBuffer);
        this.sitemeshBuffer = sitemeshBuffer;
        this.head = head;
        this.body = body;
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
            try
            {
                head.writeTo(headString);
            }
            catch (IOException e)
            {
                throw new RuntimeException("IOException occured while writing to buffer?", e);
            }
            return headString.toString();
        }
        else
        {
            return "";
        }
    }

    @Override
    public void writeBody(Writer out) throws IOException
    {
        if (body != null)
        {
            if (out instanceof SitemeshWriter)
            {
                stateCheckNoSecondaryStorage();
                ((SitemeshWriter) out).writeSitemeshBufferFragment(body);
            }
            else
            {
                ///
                // for the record its possible that the body buffer has SOME of the body but not all of it
                // because its in secondary storage.  So IF we have secondary storage then we know the body
                // was pretty big
                //
                body.writeTo(out);
                writeOutSecondaryStorage(out, sitemeshBuffer);
            }
        }
        else
        {
            //
            // if we have no body fragment it means that the whole thing is a body or there is no html or body tags.  Either way
            // we want to write everything out as is.
            //
            sitemeshBuffer.writeTo(out, 0, sitemeshBuffer.getBufferLength());
            writeOutSecondaryStorage(out, sitemeshBuffer);
        }
    }

    private void writeOutSecondaryStorage(Writer out, SitemeshBuffer sitemeshBuffer)
    {
        if (sitemeshBuffer.hasSecondaryStorage())
        {
            SecondaryStorage secondaryStorage = sitemeshBuffer.getSecondaryStorage();
            try
            {
                secondaryStorage.writeTo(out);
            }
            catch (IOException e)
            {
                throw new RuntimeException("Unable to read from SiteMesh secondary storage", e);
            }
            finally
            {
                // Do this quietly.  For example the client may terminate the stream and hence we
                // get an exception but we still want to clean up without fuss.
                secondaryStorage.cleanUp();
            }
        }

    }

    @Override
    public String getBody()
    {
        stateCheckNoSecondaryStorage();
        return super.getBody();
    }

    @Override
    public void writePage(Writer out) throws IOException
    {
        sitemeshBuffer.writeTo(out, 0, sitemeshBuffer.getBufferLength());
        writeOutSecondaryStorage(out, sitemeshBuffer);
    }

    private void stateCheckNoSecondaryStorage()
    {
        if (sitemeshBuffer.hasSecondaryStorage())
        {
            throw new IllegalStateException("You have asked for all the body in memory but its spilled over into secondary storage");
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
