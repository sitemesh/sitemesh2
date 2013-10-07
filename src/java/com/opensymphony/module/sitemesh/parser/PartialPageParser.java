package com.opensymphony.module.sitemesh.parser;

import com.opensymphony.module.sitemesh.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Page parser that doesn't parse the full page, but rather just parses the head section of the page.
 *
 * @since v2.5
 */
public class PartialPageParser implements PageParser
{
    public Page parse(char[] buffer) throws IOException
    {
        return parse(new DefaultSitemeshBuffer(buffer));
    }

    public Page parse(SitemeshBuffer buffer) throws IOException
    {
        char[] data = buffer.getCharArray();
        int length = buffer.getBufferLength();
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
                    return parseHtmlPage(buffer, position);
                }
                else
                {
                    // The whole thing is the body.
                    return new PartialPageParserHtmlPage(buffer);
                }
            }
        }
        // If we're here, we mustn't have found a tag
        return new PartialPageParserHtmlPage(buffer);
    }

    private Page parseHtmlPage(SitemeshBuffer buffer, int position)
    {

        char[] data = buffer.getCharArray();
        int length = buffer.getBufferLength();
        int bodyStart = -1;
        int bodyLength = -1;
        int headStart = -1;
        int headLength = -1;
        // Find head end and start, and body start
        Map<String, String> bodyProperties = null;
        while (position < length)
        {
            if (data[position++] == '<')
            {
                if (compareLowerCase(data, length, position, "head"))
                {
                    position = findEndOf(data, length, position + 4, ">");
                    headStart = position;
                    // Find end of head
                    position = findEndTag(position, data, length, "head");
                    headLength = position - headStart;
                    position += 7;
                }
                else if (compareLowerCase(data, length, position, "body"))
                {
                    HashSimpleMap map = new HashSimpleMap();
                    bodyStart = parseProperties(data, length, position + 4, map);
                    bodyProperties = map.getMap();
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
            int idx = headStart;
            int headEnd = headStart + headLength;
            String title = null;

            TreeMap<Integer, Integer> deletions = new TreeMap<Integer, Integer>();

            // Extract meta attributes out of head
            Map<String, String> metaAttributes = new HashMap<String, String>();
            while (idx < headEnd)
            {
                if (data[idx++] == '<')
                {
                    if (compareLowerCase(data, headEnd, idx, "meta"))
                    {
                        MetaTagSimpleMap map = new MetaTagSimpleMap();
                        idx = parseProperties(data, headEnd, idx + 4, map);
                        if (map.getName() != null && map.getContent() != null)
                        {
                            metaAttributes.put(map.getName(), map.getContent());
                        }
                    }
                }
            }

            // We need a new head buffer because we have to remove the title and content tags from it
            Map<String, String> pageProperties = new HashMap<String, String>();
            for (int i = headStart; i < headEnd; i++)
            {
                char c = data[i];
                if (c == '<')
                {
                    if (compareLowerCase(data, headEnd, i + 1, "title"))
                    {
                        int titleStart = findEndOf(data, headEnd, i + 6, ">");
                        int titleEnd = findStartOf(data, headEnd, titleStart, "<");
                        title = new String(data, titleStart, titleEnd - titleStart);
                        int titleTagEnd = titleEnd + "</title>".length();
                        deletions.put(i, titleTagEnd - i);
                        i = titleTagEnd - 1;
                    }
                    else if (compareLowerCase(data, headEnd, i + 1, "content"))
                    {
                        ContentTagSimpleMap map = new ContentTagSimpleMap();
                        int contentStart = parseProperties(data, headEnd, i + 8, map);
                        int contentEnd = findStartOf(data, headEnd, contentStart, "</content>");
                        pageProperties.put(map.getTag(), new String(data, contentStart, contentEnd - contentStart));
                        int contentTagEnd = contentEnd + "</content>".length();
                        deletions.put(i, contentTagEnd - i);
                        i = contentTagEnd - 1;
                    }
                }
            }

            return new PartialPageParserHtmlPage(buffer, new SitemeshBufferFragment(buffer, bodyStart, bodyLength), bodyProperties,
                    new SitemeshBufferFragment(buffer, headStart, headEnd - headStart, deletions), title, metaAttributes, pageProperties);
        }
        else
        {
            return new PartialPageParserHtmlPage(buffer, new SitemeshBufferFragment(buffer, bodyStart, bodyLength), bodyProperties);
        }
    }

    private int findEndTag(final int position, final char[] data, final int dataEnd, final String tagName)
    {
        String endTag = "</" + tagName + ">";
        final int remainingTagLength = endTag.length() - 1;
        int i = position;
        while (i < dataEnd - remainingTagLength)
        {
            // quickly search for closing tag markers
            if(data[i] == '<')
            {
                if (data[i + remainingTagLength] == '>')
                {
                    if (data[i + 1] == '/')
                    {
                        if (compareLowerCase(data, dataEnd, i, endTag))
                        {
                            return i;
                        }
                    }
                    // Because we found a complete tag but we know it is not what we are looking for,
                    // we can just jump over this tag.
                    i += remainingTagLength;
                }
            }
            i++;
        }
        return dataEnd;
    }

    private static boolean compareLowerCase(final char[] data, final int dataEnd, int position, String token)
    {
        int l = position + token.length();
        if (l > dataEnd)
        {
            return false;
        }
        for (int i = 0; i < token.length(); i++)
        {
            // | 32 converts from ASCII uppercase to ASCII lowercase
            char potential = data[position + i];
            char needed = token.charAt(i);
            if (isUpperCaseAscii(potential) ? (potential | 32) != needed : potential != needed)
            {
                return false;
            }
        }
        return true;
    }

    private static boolean isUpperCaseAscii(char c)
    {
        return c >= 'A' && c <= 'Z';
    }

    private static int findEndOf(final char[] data, final int dataEnd, int position, String token)
    {
        for (int i = position; i < dataEnd - token.length(); i++)
        {
            if (compareLowerCase(data, dataEnd, i, token))
            {
                return i + token.length();
            }
        }
        return dataEnd;
    }

    private static int findStartOf(final char[] data, final int dataEnd, int position, String token)
    {
        for (int i = position; i < dataEnd - token.length(); i++)
        {
            if (compareLowerCase(data, dataEnd, i, token))
            {
                return i;
            }
        }
        return dataEnd;
    }

    /**
     * Parse the properties of the current tag
     *
     * @param data     the data
     * @param dataEnd  the end index of the data
     * @param position our position in the data, this should be the first character after the tag name
     * @param map      to the map to parse the properties into
     * @return The position of the first character after the tag
     */
    private static int parseProperties(char[] data, int dataEnd, int position, SimpleMap map)
    {
        int idx = position;

        while (idx < dataEnd)
        {
            // Skip forward to the next non-whitespace character
            while (idx < dataEnd && Character.isWhitespace(data[idx]))
            {
                idx++;
            }

            // Make sure its not the end of the data or the end of the tag
            if (idx == dataEnd || data[idx] == '>' || data[idx] == '/')
            {
                break;
            }

            int startAttr = idx;

            // Find the next equals
            while (idx < dataEnd && !Character.isWhitespace(data[idx]) && data[idx] != '=' && data[idx] != '>')
            {
                idx++;
            }

            if (idx == dataEnd || data[idx] != '=')
            {
                continue;
            }

            String attrName = new String(data, startAttr, idx - startAttr);

            idx++;
            if (idx == dataEnd)
            {
                break;
            }

            int startValue = idx;
            int endValue;
            if (data[idx] == '"')
            {
                idx++;
                startValue = idx;
                while (idx < dataEnd && data[idx] != '"')
                {
                    idx++;
                }
                if (idx == dataEnd)
                {
                    break;
                }
                endValue = idx;
                idx++;
            }
            else if (data[idx] == '\'')
            {
                idx++;
                startValue = idx;
                while (idx < dataEnd && data[idx] != '\'')
                {
                    idx++;
                }
                if (idx == dataEnd)
                {
                    break;
                }
                endValue = idx;
                idx++;
            }
            else
            {
                while (idx < dataEnd && !Character.isWhitespace(data[idx]) && data[idx] != '/' && data[idx] != '>')
                {
                    idx++;
                }
                endValue = idx;
            }
            String attrValue = new String(data, startValue, endValue - startValue);
            map.put(attrName, attrValue);
        }
        // Find the end of the tag
        while (idx < dataEnd && data[idx] != '>')
        {
            idx++;
        }
        if (idx == dataEnd)
        {
            return idx;
        }
        else
        {
            // Return the first character after the end of the tag
            return idx + 1;
        }
    }

    public static interface SimpleMap
    {
        public void put(String key, String value);
    }

    public static class MetaTagSimpleMap implements SimpleMap
    {
        private String name;
        private String content;

        public void put(String key, String value)
        {
            if (key.equals("name"))
            {
                name = value;
            }
            else if (key.equals("content"))
            {
                content = value;
            }
        }

        public String getName()
        {
            return name;
        }

        public String getContent()
        {
            return content;
        }
    }

    public static class ContentTagSimpleMap implements SimpleMap
    {
        private String tag;

        public void put(String key, String value)
        {
            if (key.equals("tag"))
            {
                tag = value;
            }
        }

        public String getTag()
        {
            return tag;
        }
    }

    public static class HashSimpleMap implements SimpleMap
    {
        private final Map<String, String> map = new HashMap<String, String>();

        public void put(String key, String value)
        {
            map.put(key, value);
        }

        public Map<String, String> getMap()
        {
            return map;
        }
    }
}
