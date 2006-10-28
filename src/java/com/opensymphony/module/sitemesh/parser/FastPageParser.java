/*
 * Title:        FastPageParser
 * Description:
 *
 * This software is published under the terms of the OpenSymphony Software
 * License version 1.1, of which a copy has been included with this
 * distribution in the LICENSE.txt file.
 */

package com.opensymphony.module.sitemesh.parser;

import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.module.sitemesh.html.util.CharArray;
import com.opensymphony.module.sitemesh.util.CharArrayReader;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Very fast PageParser implementation for parsing HTML.
 *
 * <p>Produces FastPage.</p>
 *
 * @author <a href="mailto:salaman@qoretech.com">Victor Salaman</a>
 * @version $Revision: 1.13 $
 * @deprecated Use HTMLPageParser instead - it performs better and is more extensible.
 */
public final class FastPageParser implements PageParser
{
   private static final int TOKEN_NONE = -0;
   private static final int TOKEN_EOF = -1;
   private static final int TOKEN_TEXT = -2;
   private static final int TOKEN_TAG = -3;
   private static final int TOKEN_COMMENT = -4;
   private static final int TOKEN_CDATA = -5;
   private static final int TOKEN_SCRIPT = -6;
   private static final int TOKEN_DOCTYPE = -7;
   private static final int TOKEN_EMPTYTAG = -8;

   private static final int STATE_EOF = -1;
   private static final int STATE_TEXT = -2;
   private static final int STATE_TAG = -3;
   private static final int STATE_COMMENT = -4;
   private static final int STATE_TAG_QUOTE = -5;
   private static final int STATE_CDATA = -6;
   private static final int STATE_SCRIPT = -7;
   private static final int STATE_DOCTYPE = -8;

   private static final int TAG_STATE_NONE = 0;
   private static final int TAG_STATE_HTML = -1;
   private static final int TAG_STATE_HEAD = -2;
   private static final int TAG_STATE_TITLE = -3;
   private static final int TAG_STATE_BODY = -4;
   private static final int TAG_STATE_XML = -6;
   private static final int TAG_STATE_XMP = -7;

   // These hashcodes are hardcoded because swtich statements can only
   // switch on compile-time constants.
   // In theory it is possible for there to be a hashcode collision with
   // other HTML tags, however in practice it is *very* unlikely because
   // tags are generally only a few characters long and hence are likely
   // to produce unique values.

   private static final int SLASH_XML_HASH = 1518984; // "/xml".hashCode();
   private static final int XML_HASH = 118807; // "xml".hashCode();
   private static final int SLASH_XMP_HASH = 1518988; // "/xmp".hashCode();
   private static final int XMP_HASH = 118811; // "xmp".hashCode();
   private static final int HTML_HASH = 3213227; // "html".hashCode();
   private static final int SLASH_HTML_HASH = 46618714; // "/html".hashCode();
   private static final int HEAD_HASH = 3198432; // "head".hashCode();
   private static final int TITLE_HASH = 110371416; // "title".hashCode();
   private static final int SLASH_TITLE_HASH = 1455941513; // "/title".hashCode();
   private static final int PARAMETER_HASH = 1954460585; // "parameter".hashCode();
   private static final int META_HASH = 3347973; // "meta".hashCode();
   private static final int SLASH_HEAD_HASH = 46603919; // "/head".hashCode();
   private static final int FRAMESET_HASH = -1644953643; // "frameset".hashCode();
   private static final int FRAME_HASH = 97692013; // "frame".hashCode();
   private static final int BODY_HASH = 3029410; // "body".hashCode();
   private static final int SLASH_BODY_HASH = 46434897; // "/body".hashCode();
   private static final int CONTENT_HASH = 951530617; // "content".hashCode();

   public Page parse(char[] data) throws IOException
   {
      FastPage page = internalParse(new CharArrayReader(data));
      page.setVerbatimPage(data);
      return page;
   }

   public Page parse(Reader reader)
   {
      return internalParse(reader);
   }

   private FastPage internalParse(Reader reader)
   {
      CharArray _buffer    = new CharArray(4096);
      CharArray _body      = new CharArray(4096);
      CharArray _head      = new CharArray(512);
      CharArray _title     = new CharArray(128);
      Map _htmlProperties     = null;
      Map _metaProperties     = new HashMap(6);
      Map _sitemeshProperties = new HashMap(6);
      Map _bodyProperties     = null;

      CharArray _currentTaggedContent = new CharArray(1024);
      String _contentTagId = null;
      boolean tagged = false;

      boolean _frameSet = false;

      int _state = STATE_TEXT;
      int _tokenType = TOKEN_NONE;
      int _pushBack = 0;
      int _comment = 0;
      int _quote = 0;
      boolean hide = false;

      int state = TAG_STATE_NONE;
      int laststate = TAG_STATE_NONE;
      boolean doneTitle = false;

      // This tag object gets reused each iteration.
      Tag tagObject = new Tag();

      while (_tokenType != TOKEN_EOF)
      {
         if(tagged)
         {
            if(_tokenType == TOKEN_TAG || _tokenType == TOKEN_EMPTYTAG)
            {
               if(_buffer==null || _buffer.length()==0)
               {
                  _tokenType=TOKEN_NONE;
                  continue;
               }

               if (parseTag(tagObject, _buffer) == null) continue;

               if (_buffer.compareLowerSubstr("/content"))   // Note that the '/' survives the | 32 operation
               {
                  tagged = false;
                  if(_contentTagId != null)
                  {
                     state = TAG_STATE_NONE;
                     _sitemeshProperties.put(_contentTagId, _currentTaggedContent.toString());
                     _currentTaggedContent.setLength(0);
                     _contentTagId = null;
                  }
               }
               else
               {
                  _currentTaggedContent.append('<').append(_buffer).append('>');
               }
            }
            else
            {
               if(_buffer.length() > 0) _currentTaggedContent.append(_buffer);
            }
         }
         else
         {
            if(_tokenType == TOKEN_TAG || _tokenType == TOKEN_EMPTYTAG)
            {
               if(_buffer==null || _buffer.length()==0)
               {
                  _tokenType=TOKEN_NONE;
                  continue;
               }

               if(parseTag(tagObject, _buffer) == null) {
                  _tokenType=TOKEN_TEXT;
                  continue;
               }

               int tagHash = _buffer.substrHashCode();

               if(state == TAG_STATE_XML || state == TAG_STATE_XMP)
               {
                  writeTag(state, laststate, hide, _head, _buffer, _body);
                  if( (state == TAG_STATE_XML && tagHash == SLASH_XML_HASH)
                    ||(state == TAG_STATE_XMP && tagHash == SLASH_XMP_HASH) )
                  {
                     state = laststate;
                  }
               }
               else
               {
                  boolean doDefault = false;
                  switch (tagHash) {
                     case HTML_HASH:
                        if (!_buffer.compareLowerSubstr("html")) { // skip any accidental hash collisions
                           doDefault = true;
                           break;
                        }
                        state = TAG_STATE_HTML;
                        _htmlProperties = parseProperties(tagObject, _buffer).properties;
                        break;
                     case HEAD_HASH:
                        if (!_buffer.compareLowerSubstr("head")) { // skip any accidental hash collisions
                           doDefault = true;
                              break;
                        }
                        state = TAG_STATE_HEAD;
                        break;
                     case XML_HASH:
                        if (!_buffer.compareLowerSubstr("xml")) { // skip any accidental hash collisions
                           doDefault = true;
                           break;
                        }
                        laststate = state;
                        writeTag(state, laststate, hide, _head, _buffer, _body);
                        state = TAG_STATE_XML;
                        break;
                     case XMP_HASH:
                        if (!_buffer.compareLowerSubstr("xmp")) { // skip any accidental hash collisions
                           doDefault = true;
                           break;
                        }
                        laststate = state;
                        writeTag(state, laststate, hide, _head, _buffer, _body);
                        state = TAG_STATE_XMP;
                        break;
                     case TITLE_HASH:
                        if (!_buffer.compareLowerSubstr("title")) { // skip any accidental hash collisions
                           doDefault = true;
                           break;
                        }
                        if (doneTitle)
                        {
                           hide = true;
                        }
                        else
                        {
                           laststate = state;
                           state = TAG_STATE_TITLE;
                        }
                        break;
                     case SLASH_TITLE_HASH:
                        if (!_buffer.compareLowerSubstr("/title")) { // skip any accidental hash collisions
                           doDefault = true;
                           break;
                        }
                        if (doneTitle)
                        {
                           hide = false;
                        }
                        else
                        {
                           doneTitle = true;
                           state = laststate;
                        }
                        break;
                     case PARAMETER_HASH:
                        if (!_buffer.compareLowerSubstr("parameter")) { // skip any accidental hash collisions
                           doDefault = true;
                           break;
                        }
                        parseProperties(tagObject, _buffer);
                        String name = (String) tagObject.properties.get("name");
                        String value = (String) tagObject.properties.get("value");

                        if (name != null && value != null)
                        {
                           _sitemeshProperties.put(name, value);
                        }
                        break;
                     case META_HASH:
                        if (!_buffer.compareLowerSubstr("meta")) { // skip any accidental hash collisions
                           doDefault = true;
                           break;
                        }
                        CharArray metaDestination = state == TAG_STATE_HEAD ? _head : _body;
                        metaDestination.append('<');
                        metaDestination.append(_buffer);
                        metaDestination.append('>');
                        parseProperties(tagObject, _buffer);
                        name = (String) tagObject.properties.get("name");
                        value = (String) tagObject.properties.get("content");

                        if (name == null)
                        {
                           String httpEquiv = (String) tagObject.properties.get("http-equiv");

                           if (httpEquiv != null)
                           {
                              name = "http-equiv." + httpEquiv;
                           }
                        }

                        if (name != null && value != null)
                        {
                           _metaProperties.put(name, value);
                        }
                        break;
                     case SLASH_HEAD_HASH:
                        if (!_buffer.compareLowerSubstr("/head")) { // skip any accidental hash collisions
                           doDefault = true;
                           break;
                        }
                        state = TAG_STATE_HTML;
                        break;
                     case FRAME_HASH:
                        if (!_buffer.compareLowerSubstr("frame")) { // skip any accidental hash collisions
                           doDefault = true;
                           break;
                        }
                        _frameSet = true;
                        break;
                     case FRAMESET_HASH:
                        if (!_buffer.compareLowerSubstr("frameset")) { // skip any accidental hash collisions
                           doDefault = true;
                           break;
                        }
                        _frameSet = true;
                        break;
                     case BODY_HASH:
                        if (!_buffer.compareLowerSubstr("body")) { // skip any accidental hash collisions
                           doDefault = true;
                           break;
                        }
                        if (_tokenType == TOKEN_EMPTYTAG)
                        {
                           state = TAG_STATE_BODY;
                        }
                        _bodyProperties = parseProperties(tagObject, _buffer).properties;
                        break;
                     case CONTENT_HASH:
                        if (!_buffer.compareLowerSubstr("content")) { // skip any accidental hash collisions
                           doDefault = true;
                           break;
                        }
                        state = TAG_STATE_NONE;
                        Map props = parseProperties(tagObject, _buffer).properties;
                        if (props != null)
                        {
                           tagged = true;
                           _contentTagId = (String) props.get("tag");
                        }
                        break;
                     case SLASH_XMP_HASH:
                        if (!_buffer.compareLowerSubstr("/xmp")) { // skip any accidental hash collisions
                           doDefault = true;
                           break;
                        }
                        hide = false;
                        break;
                     case SLASH_BODY_HASH:
                        if (!_buffer.compareLowerSubstr("/body")) { // skip any accidental hash collisions
                           doDefault = true;
                           break;
                        }
                        state = TAG_STATE_NONE;
                        hide = true;
                        break;
                     case SLASH_HTML_HASH:
                        if (!_buffer.compareLowerSubstr("/html")) { // skip any accidental hash collisions
                           doDefault = true;
                           break;
                        }
                        state = TAG_STATE_NONE;
                        hide = true;
                        break;
                     default:
                        doDefault = true;
                  }
                  if (doDefault)
                     writeTag(state, laststate, hide, _head, _buffer, _body);
               }
            }
            else if (!hide)
            {
               if (_tokenType == TOKEN_TEXT)
               {
                  if (state == TAG_STATE_TITLE)
                  {
                     _title.append(_buffer);
                  }
                  else if (shouldWriteToHead(state, laststate))
                  {
                     _head.append(_buffer);
                  }
                  else
                  {
                     _body.append(_buffer);
                  }
               }
               else if (_tokenType == TOKEN_COMMENT)
               {
                  final CharArray commentDestination = shouldWriteToHead(state, laststate) ? _head : _body;
                  commentDestination.append("<!--");
                  commentDestination.append(_buffer);
                  commentDestination.append("-->");
               }
               else if (_tokenType == TOKEN_CDATA)
               {
                  final CharArray commentDestination = state == TAG_STATE_HEAD ? _head : _body;
                  commentDestination.append("<![CDATA[");
                  commentDestination.append(_buffer);
                  commentDestination.append("]]>");
               }
               else if (_tokenType == TOKEN_SCRIPT)
               {
                  final CharArray commentDestination = state == TAG_STATE_HEAD ? _head : _body;
                  commentDestination.append('<');
                  commentDestination.append(_buffer);
               }
            }
         }
         _buffer.setLength(0);

         start:
         while (true)
         {
            int c;

            if(_pushBack != 0)
            {
               c = _pushBack;
               _pushBack = 0;
            }
            else
            {
               try
               {
                  c = reader.read();
               }
               catch(IOException e)
               {
                  _tokenType = TOKEN_EOF;
                  break start;
               }
            }

            if(c < 0)
            {
               int tmpstate = _state;
               _state = STATE_EOF;

               if(_buffer.length() > 0 && tmpstate == STATE_TEXT)
               {
                  _tokenType = TOKEN_TEXT;
                  break start;
               }
               else
               {
                  _tokenType = TOKEN_EOF;
                  break start;
               }
            }

            switch(_state)
            {
               case STATE_TAG:
               {
                  int buflen = _buffer.length();

                  if(c == '>')
                  {
                     if (_buffer.length() > 1 && _buffer.charAt(_buffer.length() - 1) == '/')
                     {
                        _tokenType = TOKEN_EMPTYTAG;
                     }
                     else
                     {
                        _tokenType = TOKEN_TAG;
                     }
                     _state = STATE_TEXT;
                     break start;
                  }
                  else if(c == '/')
                  {
                     _buffer.append('/');
                  }
                  else if(c == '<' && buflen == 0)
                  {
                     _buffer.append("<<");
                     _state = STATE_TEXT;
                  }
                  else if(c == '-' && buflen == 2 && _buffer.charAt(1) == '-' && _buffer.charAt(0) == '!')
                  {
                     _buffer.setLength(0);
                     _state = STATE_COMMENT;
                  }
                  else if(c == '[' && buflen == 7 && _buffer.charAt(0) == '!' && _buffer.charAt(1) == '[' &&  _buffer.compareLower("cdata", 2))
                  {
                     _buffer.setLength(0);
                     _state = STATE_CDATA;
                  }
                  else if((c == 'e' || c == 'E') && buflen == 7 && _buffer.charAt(0) == '!' && _buffer.compareLower("doctyp", 1))
                  {
                     _buffer.append((char)c);
                     _state = STATE_DOCTYPE;
                  }
                  else if((c == 'T' || c == 't') && buflen == 5 && _buffer.compareLower("scrip", 0))
                  {
                     _buffer.append((char)c);
                     _state = STATE_SCRIPT;
                  }

                  else if(c == '"' || c == '\'')
                  {
                     _quote = c;
                     _buffer.append(( char ) c);
                     _state = STATE_TAG_QUOTE;
                  }
                  else
                  {
                     _buffer.append(( char ) c);
                  }
               }
               break;

               case STATE_TEXT:
               {
                  if(c == '<')
                  {
                     _state = STATE_TAG;
                     if(_buffer.length() > 0)
                     {
                        _tokenType = TOKEN_TEXT;
                        break start;
                     }
                  }
                  else
                  {
                     _buffer.append(( char ) c);
                  }
               }
               break;

               case STATE_TAG_QUOTE:
               {
                  if(c == '>')
                  {
                     _pushBack = c;
                     _state = STATE_TAG;
                  }
                  else
                  {
                     _buffer.append(( char ) c);
                     if(c == _quote)
                     {
                        _state = STATE_TAG;
                     }
                  }
               }
               break;

               case STATE_COMMENT:
               {
                  if(c == '>' && _comment >= 2)
                  {
                     _buffer.setLength(_buffer.length() - 2);
                     _comment = 0;
                     _state = STATE_TEXT;
                     _tokenType = TOKEN_COMMENT;
                     break start;
                  }
                  else if(c == '-')
                  {
                     _comment++;
                  }
                  else
                  {
                     _comment = 0;
                  }

                  _buffer.append(( char ) c);
               }
               break;

               case STATE_CDATA:
               {
                  if(c == '>' && _comment >= 2)
                  {
                     _buffer.setLength(_buffer.length() - 2);
                     _comment = 0;
                     _state = STATE_TEXT;
                     _tokenType = TOKEN_CDATA;
                     break start;
                  }
                  else if(c == ']')
                  {
                     _comment++;
                  }
                  else
                  {
                     _comment = 0;
                  }

                  _buffer.append(( char ) c);
               }
               break;

               case STATE_SCRIPT:
               {
                  _buffer.append((char) c);
                  if (c == '<')
                  {
                     _comment = 0;
                  }
                  else if ((c == '/' && _comment == 0)
                     ||((c == 's' || c == 'S' ) && _comment == 1)
                     ||((c == 'c' || c == 'C' ) && _comment == 2)
                     ||((c == 'r' || c == 'R' ) && _comment == 3)
                     ||((c == 'i' || c == 'I' ) && _comment == 4)
                     ||((c == 'p' || c == 'P' ) && _comment == 5)
                     ||((c == 't' || c == 'T' ) && _comment == 6)
                  )
                  {
                     _comment++;
                  }
                  else if(c == '>' && _comment >= 7)
                  {
                     _comment = 0;
                     _state = STATE_TEXT;
                     _tokenType = TOKEN_SCRIPT;
                     break start;
                  }
               }
               break;

               case STATE_DOCTYPE:
               {
                  _buffer.append((char) c);
                  if (c == '>')
                  {
                     _state = STATE_TEXT;
                     _tokenType = TOKEN_DOCTYPE;
                     break start;
                  }
                  else {
                    _comment = 0;
                  }
               }
               break;
            }
         }
      }

      // Help the GC
      _currentTaggedContent = null;
      _buffer = null;

      return new FastPage(_sitemeshProperties,
                          _htmlProperties,
                          _metaProperties,
                          _bodyProperties,
                          _title.toString().trim(),
                          _head.toString().trim(),
                          _body.toString().trim(),
                          _frameSet);
   }

   private static void writeTag(int state, int laststate, boolean hide, CharArray _head, CharArray _buffer, CharArray _body) {
      if (!hide)
      {
         if (shouldWriteToHead(state, laststate))
         {
            _head.append('<').append(_buffer).append('>');
         }
         else
         {
            _body.append('<').append(_buffer).append('>');
         }
      }
   }

   private static boolean shouldWriteToHead(int state, int laststate)
   {
      return state == TAG_STATE_HEAD
             ||(laststate == TAG_STATE_HEAD && (state == TAG_STATE_XML || state == TAG_STATE_XMP));
   }

   /**
    * Populates a {@link Tag} object using data from the supplied {@link CharArray}.
    *
    * The supplied tag parameter is reset and reused - this avoids excess object
    * creation which hwlps performance.
    *
    * @return the same tag instance that was passed in, except it will be populated
    * with a new <tt>name</tt> value (and the corresponding <tt>nameEndIdx</tt> value).
    * However if the tag contained nathing but whitespace, this method will return
    * <tt>null</tt>.
    */
   private Tag parseTag(Tag tag, CharArray buf)
   {
      int len = buf.length();
      int idx = 0;
      int begin;

      // Skip over any leading whitespace in the tag
      while (idx < len && Character.isWhitespace(buf.charAt(idx))) idx++;

      if(idx == len) return null;

      // Find out where the non-whitespace characters end. This will give us the tag name.
      begin = idx;
      while (idx < len && !Character.isWhitespace(buf.charAt(idx))) idx++;

      // Mark the tag name as a substring within the buffer. This allows us to perform
      // a substring comparison against it at a later date
      buf.setSubstr(begin, buf.charAt(idx - 1) == '/' ? idx - 1 : idx);

      // Remember where the name finishes so we can pull out the properties later if need be
      tag.nameEndIdx = idx;

      return tag;
   }

   /**
    * This is called when we need to extract the properties for the tag from the tag's HTML.
    * We only call this when necessary since it has quite a lot of overhead.
    *
    * @param tag the tag that is currently being processed. This should be the
    * tag that was returned as a result of a call to {@link #parseTag(FastPageParser.Tag, CharArray)}
    * (ie, it has the <tt>name</tt> and <tt>nameEndIdx</tt> fields set correctly for the
    * tag in question. The <tt>properties</tt> field can be in an undefined state - it
    * will get replaced regardless).
    * @param buffer a <tt>CharArray</tt> containing the entire tag that is being parsed.
    * @return the same tag instance that was passed in, only it will now be populated
    * with any properties that were specified in the tag's HTML.
    */
   private static Tag parseProperties(Tag tag, CharArray buffer)
   {
      int len = buffer.length();
      int idx = tag.nameEndIdx;

      // Start with an empty hashmap. A new HashMap is lazy-created if we happen to find any properties
      tag.properties = Collections.EMPTY_MAP;
      int begin;
      while (idx < len)
      {
         // Skip forward to the next non-whitespace character
         while (idx < len && Character.isWhitespace(buffer.charAt(idx))) idx++;

         if(idx == len) continue;

         begin = idx;
         if(buffer.charAt(idx) == '"')
         {
            idx++;
            while (idx < len && buffer.charAt(idx) != '"') idx++;
            if(idx == len) continue;
            idx++;
         }
         else if(buffer.charAt(idx) == '\'')
         {
            idx++;
            while (idx < len && buffer.charAt(idx) != '\'') idx++;
            if(idx == len) continue;
            idx++;
         }
         else
         {
            while (idx < len && !Character.isWhitespace(buffer.charAt(idx)) && buffer.charAt(idx) != '=') idx++;
         }

         // Mark the substring. This is the attribute name
         buffer.setSubstr(begin, idx);

         if(idx < len && Character.isWhitespace(buffer.charAt(idx)))
         {
            while (idx < len && Character.isWhitespace(buffer.charAt(idx))) idx++;
         }

         if(idx == len || buffer.charAt(idx) != '=') continue;

         idx++;

         if(idx == len) continue;

         while(idx < len && (buffer.charAt(idx) == '\n' || buffer.charAt(idx) == '\r')) idx++;

         if(buffer.charAt(idx) == ' ')
         {
            while (idx < len && Character.isWhitespace(buffer.charAt(idx))) idx++;
            if(idx == len || (buffer.charAt(idx) != '"' && buffer.charAt(idx) != '"')) continue;
         }

         begin = idx;
         int end;
         if(buffer.charAt(idx) == '"')
         {
            idx++;
            begin = idx;
            while (idx < len && buffer.charAt(idx) != '"') idx++;
            if(idx == len) continue;
            end = idx;
            idx++;
         }
         else if(buffer.charAt(idx) == '\'')
         {
            idx++;
            begin = idx;
            while (idx < len && buffer.charAt(idx) != '\'') idx++;
            if(idx == len) continue;
            end = idx;
            idx++;
         }
         else
         {
            while (idx < len && !Character.isWhitespace(buffer.charAt(idx))) idx++;
            end = idx;
         }
         // Extract the name and value as String objects and add them to the property map
         String name = buffer.getLowerSubstr();
         String value = buffer.substring(begin, end);

         tag.addProperty(name, value);
      }
      return tag;
   }

   private class Tag
   {
      // The index where the name string ends. This is used as the starting
      // offet if we need to continue processing to find the tag's properties
      public int nameEndIdx = 0;

      // This holds a map of the various properties for a particular tag.
      // This map is only populated when required - normally it will remain empty
      public Map properties = Collections.EMPTY_MAP;

      /**
       * Adds a name/value property pair to this tag. Each property that is
       * added represents a property that was parsed from the tag's HTML.
       */
      public void addProperty(String name, String value)
      {
        if(properties==Collections.EMPTY_MAP)
        {
          properties = new HashMap(8);
        }
        properties.put(name, value);
      }
   }
}
