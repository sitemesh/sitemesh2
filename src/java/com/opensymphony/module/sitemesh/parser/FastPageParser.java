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

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Very fast PageParser implementation for parsing HTML.
 *
 * <p>Produces FastPage.</p>
 *
 * @author <a href="mailto:salaman@qoretech.com">Victor Salaman</a>
 * @version $Revision: 1.1 $
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

   public Page parse(char[] data) throws IOException
   {
      Reader reader = null;

      reader = new BufferedReader(new CharArrayReader(data));

      FastPage page = internalParse(reader);
      page.setVerbatimPage(data);
      return page;
   }

   public Page parse(Reader reader)
   {
      return internalParse(reader);
   }

   private FastPage internalParse(Reader reader)
   {
      StringBuffer _buffer    = new StringBuffer(4096);
      StringBuffer _body      = new StringBuffer(4096);
      StringBuffer _head      = new StringBuffer(512);
      StringBuffer _title     = new StringBuffer(128);
      Map _htmlProperties     = null;
      Map _metaProperties     = new HashMap(6);
      Map _sitemeshProperties = new HashMap(6);
      Map _bodyProperties     = null;

      StringBuffer _currentTaggedContent = new StringBuffer(1024);
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

               Tag tagObject = parseTag(_buffer);
               String tag = tagObject.name;
               if(tag==null) continue;

               if(tag.equals("/content"))
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
                  _currentTaggedContent.append('<').append(_buffer.toString()).append('>');
               }
            }
            else
            {
               if(_buffer.length() > 0) _currentTaggedContent.append(_buffer.toString());
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
               Tag tagObject = parseTag(_buffer);

               if(tagObject == null) {
                  _tokenType=TOKEN_TEXT;
                  continue;
               }

               String tag = tagObject.name;

               if(tag == null)
                  continue;
               else if(state == TAG_STATE_XML || state == TAG_STATE_XMP)
               {
                  writeTag(state, laststate, hide, _head, _buffer, _body);
                  if( (state == TAG_STATE_XML && tag.equals("/xml"))
                    ||(state == TAG_STATE_XMP && tag.equals("/xmp")) )
                  {
                     state = laststate;
                  }
               }
               else if(tag.equals("html"))
               {
                  state = TAG_STATE_HTML;
                  _htmlProperties = tagObject.properties;
               }
               else if(tag.equals("head"))
               {
                  if (_tokenType == TOKEN_EMPTYTAG)
                  {
                     state = TAG_STATE_BODY;
                  }
                  state = TAG_STATE_HEAD;
               }
               else if(tag.equals("xml"))
               {
                  laststate = state;
                  writeTag(state, laststate, hide, _head, _buffer, _body);
                  state = TAG_STATE_XML;
               }
               else if(tag.equals("xmp"))
               {
                  laststate = state;
                  writeTag(state, laststate, hide, _head, _buffer, _body);
                  state = TAG_STATE_XMP;
               }
               else if(tag.equals("title"))
               {
                  if ( doneTitle )
                  {
                     hide = true;
                  }
                  else
                  {
                     laststate = state;
                     state = TAG_STATE_TITLE;
                  }
               }
               else if(tag.equals("/title"))
               {
                  if ( doneTitle )
                  {
                     hide = false;
                  }
                  else
                  {
                     doneTitle = true;
                     state = laststate;
                  }
               }
               else if(tag.equals("parameter"))
               {
                  String name = ( String ) tagObject.properties.get("name");
                  String value = ( String ) tagObject.properties.get("value");

                  if(name != null && value != null)
                  {
                     _sitemeshProperties.put(name, value);
                  }
               }
               else if(tag.equals("meta"))
               {
                  StringBuffer metaDestination = state == TAG_STATE_HEAD ? _head : _body;
                  metaDestination.append('<');
                  metaDestination.append(_buffer.toString());
                  metaDestination.append('>');

                  String name = ( String ) tagObject.properties.get("name");
                  String value = ( String ) tagObject.properties.get("content");

                  if(name == null)
                  {
                     String httpEquiv = ( String ) tagObject.properties.get("http-equiv");

                     if(httpEquiv != null)
                     {
                        name = "http-equiv." + httpEquiv;
                     }
                  }

                  if(name != null && value != null)
                  {
                     _metaProperties.put(name, value);
                  }
               }
               else if(tag.equals("/head"))
               {
                  state = TAG_STATE_HTML;
               }
               else if(tag.equals("frameset") || tag.equals("frame"))
               {
                  _frameSet = true;
               }
               else if(tag.equals("body"))
               {
                  if (_tokenType == TOKEN_EMPTYTAG)
                  {
                     state = TAG_STATE_BODY;
                  }
                  _bodyProperties = tagObject.properties;
               }
               else if(tag.equals("content"))
               {
                  state = TAG_STATE_NONE;
                  Map props = tagObject.properties;
                  if(props != null)
                  {
                     tagged = true;
                     _contentTagId = ( String ) props.get("tag");
                  }
               }
               else if(tag.equals("xmp"))
               {
                  hide = true;
               }
               else if(tag.equals("/xmp"))
               {
                  hide = false;
               }
               else if(tag.equals("/body"))
               {
                  state = TAG_STATE_NONE;
                  hide = true;
               }
               else if(tag.equals("/html"))
               {
                  state = TAG_STATE_NONE;
                  hide = true;
               }
               else
               {
                  writeTag(state, laststate, hide, _head, _buffer, _body);
               }
            }
            else if(_tokenType == TOKEN_TEXT)
            {
               if(state == TAG_STATE_TITLE)
               {
                  if(!hide)
                  {
                     _title.append(_buffer.toString());
                  }
               }
               else if(shouldWriteToHead(state, laststate))
               {
                  if(!hide)
                  {
                     _head.append(_buffer.toString());
                  }
               }
               else
               {
                  if(!hide)
                  {
                     _body.append(_buffer.toString());
                  }
               }
            }
            else if(_tokenType == TOKEN_COMMENT)
            {
               if(!hide)
               {
                  final StringBuffer commentDestination = shouldWriteToHead(state, laststate) ? _head : _body;
                  commentDestination.append("<!--");
                  commentDestination.append(_buffer.toString());
                  commentDestination.append("-->");
               }
            }
            else if(_tokenType == TOKEN_CDATA)
            {
               if(!hide)
               {
                  final StringBuffer commentDestination = state == TAG_STATE_HEAD ? _head : _body;
                  commentDestination.append("<![CDATA[");
                  commentDestination.append(_buffer.toString());
                  commentDestination.append("]]>");
               }
            }
            else if(_tokenType == TOKEN_SCRIPT)
            {
               if(!hide)
               {
                  final StringBuffer commentDestination = state == TAG_STATE_HEAD ? _head : _body;
                  commentDestination.append('<');
                  commentDestination.append(_buffer.toString());
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
                  else if(c == '[' && buflen == 7 && _buffer.substring(0, 7).equalsIgnoreCase("![CDATA"))
                  {
                     _buffer.setLength(0);
                     _state = STATE_CDATA;
                  }
                  else if((c == 'e' || c == 'E') && buflen == 7 && _buffer.substring(0, 7).equalsIgnoreCase("!DOCTYP"))
                  {
                     _buffer.append((char)c);
                     _state = STATE_DOCTYPE;
                  }
                  else if((c == 'T' || c == 't') && buflen == 5 && _buffer.substring(0, 5).equalsIgnoreCase("SCRIP"))
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

   private static void writeTag(int state, int laststate, boolean hide, StringBuffer _head, StringBuffer _buffer, StringBuffer _body) {
      if(shouldWriteToHead(state, laststate))
      {
         if(!hide)
         {
            _head.append('<');
            _head.append(_buffer.toString());
            _head.append('>');
         }
      }
      else
      {
         if(!hide)
         {
            _body.append('<');
            _body.append(_buffer.toString());
            _body.append('>');
         }
      }
   }

   private static boolean shouldWriteToHead(int state, int laststate)
   {
      return state == TAG_STATE_HEAD
             ||(laststate == TAG_STATE_HEAD && (state == TAG_STATE_XML || state == TAG_STATE_XMP));
   }

   private Tag parseTag(StringBuffer buf)
   {
      String buffer = buf.toString();
      int len = buf.length();
      int idx = 0;
      int begin = 0;

      Tag tag = new Tag();

      while (idx < len && Character.isWhitespace(buffer.charAt(idx))) idx++;

      if(idx == len) return null;

      begin = idx;
      while (idx < len && !Character.isWhitespace(buffer.charAt(idx))) idx++;
      String name = buffer.substring(begin, buffer.charAt(idx - 1) == '/' ? idx - 1 : idx);

      tag.name = name == null ? null : name.toLowerCase();

      while (idx < len && Character.isWhitespace(buffer.charAt(idx))) idx++;

      if(idx == len) return tag;

      return parseProperties(tag, buffer, idx);
   }

   private static Tag parseProperties(Tag tag, String buffer, int idx)
   {
      int len = buffer.length();
      int begin = 0;

      while (idx < len)
      {
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

         String name = buffer.substring(begin, idx).toLowerCase();

         if(idx < len && Character.isWhitespace(buffer.charAt(idx)))
         {
            while (idx < len && Character.isWhitespace(buffer.charAt(idx))) idx++;
         }

         if(idx == len || buffer.charAt(idx) != '=')
         {
            continue;
         }
         idx++;

         if(idx == len) continue;

         if(buffer.charAt(idx) == ' ')
         {
            while (idx < len && Character.isWhitespace(buffer.charAt(idx))) idx++;
            if(idx == len || (buffer.charAt(idx) != '"' && buffer.charAt(idx) != '"')) continue;
         }

         begin = idx;
         int end = begin;
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

         String value = buffer.substring(begin, end);

         tag.addProperty(name, value);
      }
      return tag;

   }

   class Tag
   {
      public String name;
      public Map properties = new HashMap(8);

      public void addProperty(String name, String value)
      {
         properties.put(name, value);
      }
   }
}
