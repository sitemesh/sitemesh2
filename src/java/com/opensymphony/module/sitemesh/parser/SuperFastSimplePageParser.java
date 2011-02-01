package com.opensymphony.module.sitemesh.parser;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.PageParser;

/**
 * Super fast page parser.  It uses a single buffer, and never copies anything, apart from the title, meta attributes
 * and body properties. This page parser makes several assumptions:
 * <p/>
 * <ul> <li>If the first tag is an html tag, it's an HTML page, otherwise, it's a fragment, and no head/title/etc
 * parsing will be done.</li>
 * </ul>
 *
 * @since v2.4
 */
public class SuperFastSimplePageParser implements PageParser
{
    private static final Pattern META_PATTERN = Pattern.compile(
            "<meta\\s+([\\w-]+)=[\"']([^\"']*)[\"']\\s+([\\w-]+)=[\"']([^\"']*)[\"']\\s*/?>", Pattern.CASE_INSENSITIVE);

    public Page parse(final char[] data) throws IOException
    {
        return parse(data, data.length);
    }
    
    public Page parse(final char[] data, final int length) throws IOException
    {
        int position = 0;
        while (position < data.length)
        {
            if (data[position++] == '<')
            {
                if (position < data.length && data[position] == '!')
                {
                    // Ignore doctype
                    continue;
                }
                if (compareLowerCase(data, length, position, "html"))
                {
                    // It's an HTML page, handle HTML pages
                    return parseHtmlPage(data, length, position);
                }
                else
                {
                    // The whole thing is the body.
                    return new SuperFastPage(data, length, 0, length);
                }
            }
        }
        // If we're here, we mustn't have found a tag
        return new SuperFastPage(data, length, 0, length);
    }

    private Page parseHtmlPage(final char[] data, final int length, int position)
    {
        int bodyStart = -1;
        int bodyLength = -1;
        int headStart = -1;
        int headLength = -1;
        // Find head end and start, and body start
        while (position < length)
        {
            if (data[position++] == '<')
            {
                if (compareLowerCase(data, length, position, "head"))
                {
                    position = findEndOf(data, length, position + 4, ">");
                    headStart = position;
                    // Find end of head
                    position = findStartOf(data, length, position, "</head>");
                    headLength = position - headStart;
                    position += 7;
                }
                else if (compareLowerCase(data, length, position, "body"))
                {
                    bodyStart = findEndOf(data, length, position + 4, ">");
                    break;
                }
            }
        }

        if (bodyStart < 0)
        {
            // No body found
            bodyStart = length;
            bodyLength = 0;
        }
        else
        {
            for (int i = length - 8; i > bodyStart; i--)
            {
                if (compareLowerCase(data, length, i, "</body>"))
                {
                    bodyLength = i - bodyStart;
                    break;
                }
            }
            if (bodyLength == -1)
            {
                bodyLength = length - bodyStart;
            }
        }

        if (headLength > 0)
        {
            String title = null;
            // Extract title and meta properties.  This should be a small amount of data, so regexs are fine
            CharBuffer buffer = CharBuffer.wrap(data, headStart, headLength);
            Matcher matcher = META_PATTERN.matcher(buffer);
            Map<String, String> metaAttributes = new HashMap<String, String>();
            while (matcher.find())
            {
                if (matcher.group(1).equals("content"))
                {
                    metaAttributes.put(matcher.group(4), matcher.group(2));
                }
                else
                {
                    metaAttributes.put(matcher.group(2), matcher.group(4));                    
                }
            }

            // We need a new head buffer because we have to remove the title from it
            CharArrayWriter head = new CharArrayWriter();
            for (int i = headStart; i < headStart + headLength; i++)
            {
                char c = data[i];
                if (c == '<')
                {
                    if (compareLowerCase(data, headLength, i + 1, "title"))
                    {
                        int titleStart = findEndOf(data, headLength, i + 6, ">");
                        int titleEnd = findStartOf(data, headLength, titleStart, "<");
                        title = new String(data, titleStart, titleEnd - titleStart);
                        i = titleEnd + "</title>".length() - 1;
                    }
                    else
                    {
                        head.append(c);
                    }
                }
                else
                {
                    head.append(c);
                }
            }

            return new SuperFastHtmlPage(data, length, bodyStart, bodyLength, head.toCharArray(), title, metaAttributes);
        }
        else
        {
            return new SuperFastPage(data, length, bodyStart, bodyLength);
        }
    }

    private static boolean compareLowerCase(final char[] data, final int length, int position, String token)
    {
        int l = position + token.length();
        if (l > length)
        {
            return false;
        }
        for (int i = 0; i < token.length(); i++)
        {
            // | 32 converts from ASCII uppercase to ASCII lowercase
            char potential = data[position + i];
            char needed = token.charAt(i);
            if ((Character.isLetter(potential) && (potential | 32) != needed) || potential != needed)
            {
                return false;
            }
        }
        return true;
    }

    private static int findEndOf(final char[] data, final int length, int position, String token)
    {
        for (int i = position; i < length - token.length(); i++)
        {
            if (compareLowerCase(data, length, i, token))
            {
                return i + token.length();
            }
        }
        return length;
    }

    private static int findStartOf(final char[] data, final int length, int position, String token)
    {
        for (int i = position; i < length - token.length(); i++)
        {
            if (compareLowerCase(data, length, i, token))
            {
                return i;
            }
        }
        return length;
    }
}
