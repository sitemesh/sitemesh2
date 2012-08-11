package com.opensymphony.module.sitemesh.scalability.secondarystorage;

import com.opensymphony.module.sitemesh.SitemeshBuffer;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;

/**
 */
public class SecondaryStorageBufferWriterTest extends TestCase
{
    private PrintWriter pw;
    private StringWriter capture;

    public void testBadBodyData() throws Exception
    {
        assertBadBodyParsing("");
        assertBadBodyParsing("<html");
        assertBadBodyParsing("<html <body>"); // doesnt close - its not a tag
        assertBadBodyParsing("<html></html>");
        assertBadBodyParsing("<html></body>");
        assertBadBodyParsing("<html><bodyThatIsReally>");
        assertBadBodyParsing("<html><bodyThatIsReally='true'>");
        assertBadBodyParsing("<html><bo\ndy>Data By NewLined</body></html>");
        assertBadBodyParsing("<html><bo\tdy>Data By NewLined</body></html>");
        assertBadBodyParsing("<html><bo dy>Data By NewLined</body></html>");
    }

    public void testGoodBodyData() throws Exception
    {
        assertGoodBodyParsing("<html><body>Some Body Data</body>");
        assertGoodBodyParsing("<html><body with='attributes'>Some Body Data</body>");
        assertGoodBodyParsing("<html><body with='attributes' and more=\"attributes\" >Some Body Data</body>");
        assertGoodBodyParsing("<html><body \t\n >Some Body Data</body>");
        assertGoodBodyParsing("<html><body>Some Body Data");

    }

    private void assertBadBodyParsing(final String input)
    {
        assertBodyParsing(input, false, false);
    }

    private void assertGoodBodyParsing(final String input)
    {
        assertBodyParsing(input, true, false);
    }

    private void assertBodyParsing(final String input, boolean expectedInsideBody, boolean expectedSecondaryStorage)
    {
        com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorageBufferWriter writer = new com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorageBufferWriter(new InMemorySecondaryStorage(2000));

        pw = new PrintWriter(writer);

        pw.print(input);

        assertEquals(expectedInsideBody, writer.isInsideBody());
        assertEquals(expectedSecondaryStorage, writer.isHasWrittenToStorage());

        StringBuilder sb = toStringBuilder(writer.getSitemeshBuffer());
        assertEquals(input, sb.toString());
    }


    public void testStateOfWriter() throws Exception
    {
        com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorageBufferWriter writer = new com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorageBufferWriter(new InMemorySecondaryStorage(15));

        pw = new PrintWriter(writer);

        assertFalse(writer.isInsideBody());
        assertFalse(writer.isHasWrittenToStorage());

        pw.print("<html><title>X<title>");
        assertFalse(writer.isInsideBody());
        assertFalse(writer.isHasWrittenToStorage());

        pw.print("</head><body>");
        assertTrue(writer.isInsideBody());
        assertFalse(writer.isHasWrittenToStorage());

        pw.print(generateStr(100));
        assertTrue(writer.isInsideBody());
        assertTrue(writer.isHasWrittenToStorage());

        pw.print("</body></html>");
        assertTrue(writer.isInsideBody());
        assertTrue(writer.isHasWrittenToStorage());
    }

    public void testNoSpillover() throws Exception
    {
        com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorageBufferWriter writer = new com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorageBufferWriter(new InMemorySecondaryStorage(2000));

        capture = new StringWriter();
        pw = new PrintWriter(new TeeWriter(writer, capture));

        pw.print("<html><title>X<title></body></html>");

        SitemeshBuffer sitemeshBuffer = writer.getSitemeshBuffer();

        assertFalse(sitemeshBuffer.hasSecondaryStorage());

        assertBufferSsTheSame(sitemeshBuffer, capture.toString());
    }

    public void testSpillover() throws Exception
    {
        com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorageBufferWriter writer = new SecondaryStorageBufferWriter(new InMemorySecondaryStorage(20));

        capture = new StringWriter();
        pw = new PrintWriter(new TeeWriter(writer, capture));

        pw.print("<html><title>X<title>");
        pw.print("</head><body>");
        pw.print(generateStr(1000));
        pw.print("</body></html>");

        SitemeshBuffer sitemeshBuffer = writer.getSitemeshBuffer();

        assertTrue(sitemeshBuffer.hasSecondaryStorage());

        assertBufferSsTheSame(sitemeshBuffer, capture.toString());
    }

    private void assertBufferSsTheSame(SitemeshBuffer sitemeshBuffer, String expected)
    {
        StringBuilder sb = toStringBuilder(sitemeshBuffer);
        if (sitemeshBuffer.hasSecondaryStorage())
        {
            addTo(sb, sitemeshBuffer.getSecondaryStorage());
        }
        assertEquals(expected, sb.toString());
    }

    private StringBuilder toStringBuilder(SitemeshBuffer sitemeshBuffer)
    {
        char[] charArray = sitemeshBuffer.getCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sitemeshBuffer.getBufferLength(); i++)
        {
            sb.append(charArray[i]);
        }
        return sb;
    }

    private void addTo(StringBuilder sb, SecondaryStorage secondaryStorage)
    {
        StringWriter sw = new StringWriter();
        try
        {
            secondaryStorage.writeTo(sw);
            sb.append(sw.toString());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    private String generateStr(int i)
    {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < i; j++)
        {
            sb.append("X");
        }
        return sb.toString();
    }

}
