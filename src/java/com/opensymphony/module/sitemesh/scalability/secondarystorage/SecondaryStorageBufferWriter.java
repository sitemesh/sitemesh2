package com.opensymphony.module.sitemesh.scalability.secondarystorage;

import com.opensymphony.module.sitemesh.DefaultSitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.SitemeshWriter;
import com.opensymphony.module.sitemesh.util.CharArrayWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.TreeMap;

/**
 * This writer allows for secondary storage to be use If the body of the response its really large
 * <p/>
 * It will ensure that everything up to the <body> tage is in memory before spilling over, so that further parsing can
 * use memory access to find important page details.  But if the page gets really large then it will spill into the
 * secondary storage area.
 * <p/>
 * This of course falls down if the <head> section was megabytes in size say but we are deliberately not catering for
 * this situation and hence it would be worse than SiteMesh without spill over.
 */
public class SecondaryStorageBufferWriter extends CharArrayWriter implements SitemeshWriter
{
    private final TreeMap<Integer, SitemeshBufferFragment> fragments = new TreeMap<Integer, SitemeshBufferFragment>();

    private SecondaryStorage secondaryStorage;

    private long charsSoFar = 0;
    private long memoryLimit;

    private boolean insideBody = false;
    private boolean insideTag = false;
    private boolean hasWrittenToStorage = false;
    private StringBuilder currentTag = new StringBuilder();

    public SecondaryStorageBufferWriter(SecondaryStorage secondaryStorage)
    {
        this(1024 * 4, secondaryStorage);
    }

    public SecondaryStorageBufferWriter(int initialBufferSize, SecondaryStorage secondaryStorage)
    {
        super(initialBufferSize);
        this.memoryLimit = secondaryStorage.getMemoryLimitBeforeUse();
        this.secondaryStorage = secondaryStorage;
    }

    // visible for testing
    boolean isInsideBody()
    {
        return insideBody;
    }

    // visible for testing
    boolean isHasWrittenToStorage()
    {
        return hasWrittenToStorage;
    }

    private void parseChar(int c)
    {
        if (insideBody)
        {
            return;
        }
        if (!insideTag)
        {
            if (c == '<')
            {
                currentTag.setLength(0);
                insideTag = true;
                return;
            }
        }
        if (insideTag)
        {
            if (c == '>')
            {
                // ok we have a tag.  Was it the body tag
                String tag = makeTag(currentTag);
                if (tag.equals("body"))
                {
                    insideBody = true;
                }
                currentTag.setLength(0);
                insideTag = false;
                return;
            }
            else
            {
                currentTag.append((char) c);
            }
        }
    }

    private String makeTag(final StringBuilder sb)
    {
        StringBuilder tag = new StringBuilder();
        for (int i = 0; i < sb.length(); i++)
        {
            char c = sb.charAt(i);
            if (Character.isWhitespace(c))
            {
                break;
            }
            tag.append(c);
        }
        return tag.toString().toLowerCase();
    }

    private boolean isSpillEnabled()
    {
        return memoryLimit > 0;
    }

    private boolean weShouldSpillToStorage()
    {
        return isSpillEnabled() && insideBody && charsSoFar > memoryLimit;
    }

    public Writer getUnderlyingWriter()
    {
        return this;
    }

    @Override
    public void write(int c)
    {
        if (!isSpillEnabled())
        {
            super.write(c);
        }
        else
        {
            if (weShouldSpillToStorage())
            {
                try
                {
                    secondaryStorage.write(c);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
                hasWrittenToStorage = true;
            }
            else
            {
                parseChar(c);
                super.write(c);
            }
        }
        charsSoFar += 1;
    }

    @Override
    public void write(char[] chars, int off, int len)
    {
        if (!isSpillEnabled())
        {
            super.write(chars, off, len);
        }
        else
        {
            if (weShouldSpillToStorage())
            {
                try
                {
                    secondaryStorage.write(chars, off, len);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
                hasWrittenToStorage = true;
            }
            else
            {
                for (int i = off; i < len; i++)
                {
                    parseChar(chars[i]);
                }
                super.write(chars, off, len);
            }
        }
        charsSoFar += (len - off);
    }

    @Override
    public void write(char[] chars) throws IOException
    {
        write(chars, 0, chars.length);
    }

    @Override
    public void write(String str, int off, int len)
    {
        if (!isSpillEnabled())
        {
            super.write(str, off, len);
        }
        else
        {
            if (weShouldSpillToStorage())
            {
                try
                {
                    secondaryStorage.write(str, off, len);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
                hasWrittenToStorage = true;
            }
            else
            {
                for (int i = off; i < len; i++)
                {
                    parseChar(str.charAt(i));
                }
                super.write(str, off, len);
            }
        }
        charsSoFar += (len - off);
    }

    @Override
    public void write(String str) throws IOException
    {
        write(str, 0, str.length());
    }

    public boolean writeSitemeshBufferFragment(SitemeshBufferFragment bufferFragment) throws IOException
    {
        fragments.put(count, bufferFragment);
        return false;
    }

    public SitemeshBuffer getSitemeshBuffer()
    {
        if (!hasWrittenToStorage)
        {
            // so we may have got here and the memory cache contains all the data including the body
            // and hence we can read it all in memory as per usual
            // this is the fastest option and has a decent memory profile
            return new DefaultSitemeshBuffer(buf, count, fragments);
        }
        else
        {
            // turns out the response was too big and hence the contents have spilled over to disk
            return new DefaultSitemeshBuffer(buf, count, fragments, secondaryStorage);

        }
    }
}
