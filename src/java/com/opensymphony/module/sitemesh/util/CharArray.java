package com.opensymphony.module.sitemesh.util;

/**
 * A leaner, meaner version of StringBuffer.
 * <p/>
 * It provides basic functionality to handle dynamically-growing
 * char arrays as quickly as possible. This class is not threadsafe.
 * 
 * @author Chris Miller
 */
public class CharArray
{
   int size = 0;
   char[] buffer;

   /**
    * Constructs a CharArray that is initialized to the specified size.
    *
    * Do not pass in a negative value because there is no bounds checking!
    */
   public CharArray(int size)
   {
      buffer = new char[size];
   }

   /**
    * Returns a String represenation of the character array.
    */
   public String toString()
   {
      return new String(buffer, 0, size);
   }

   /**
    * Returns the character that is at the specified position in the array.
    *
    * There is no bounds checking on this method so be sure to pass in a
    * sensible value.
    */
   public char charAt(int pos)
   {
      return buffer[pos];
   }

   /**
    * Changes the size of the character array to the value specified.
    *
    * If the new size is less than the current size, the data in the
    * internal array will be truncated. If the new size is &lt;= 0,
    * the array will be reset to empty (but, unlike StringBuffer, the
    * internal array will NOT be shrunk). If the new size is &gt the
    * current size, the array will be padded out with null characters
    * (<tt>'&#92;u0000'</tt>).
    *
    * @param newSize the new size of the character array
    */
   public void setLength(int newSize)
   {
      if (newSize < 0)
      {
         newSize = 0;
      }

      if (newSize <= size)
      {
         size = newSize;
      }
      else
      {
         if (newSize >= buffer.length)
            grow(newSize);
         // Pad the array
         for (; size < newSize; size++)
                buffer[size] = '\0';
      }
   }

   /**
    * Returns the current length of the character array.
    */
   public int length()
   {
      return size;
   }

   /**
    * Appends an existing CharArray on to this one.
    *
    * Passing in a <tt>null</tt> CharArray will result in a <tt>NullPointerException</tt>.
    */
   public CharArray append(CharArray chars)
   {
      return append(chars.buffer, chars.size);
   }

   /**
    * Appends the supplied characters to the end of the array.
    */
   public CharArray append(char[] chars)
   {
      return append(chars, chars.length);
   }

   private CharArray append(char[] chars, int length)
   {
      int requiredSize = length + size;
      if (requiredSize >= buffer.length)
         grow(requiredSize);
      System.arraycopy(chars, 0, buffer, size, length);
      size = requiredSize;
      return this;
   }

   /**
    * Appends a single character to the end of the character array.
    */
   public CharArray append(char c)
   {
      if (buffer.length == size)
         grow(0);
      buffer[size++] = c;
      return this;
   }

   /**
    * Appends the supplied string to the end of this character array.
    *
    * Passing in a <tt>null</tt> string will result in a <tt>NullPointerException</tt>.
    */
   public CharArray append(String str)
   {
      int requiredSize = str.length() + size;
      if (requiredSize >= buffer.length)
         grow(requiredSize);

      for (int i = 0; i < str.length(); i++)
         buffer[size + i] = str.charAt(i);

      size = requiredSize;
      return this;
   }

   /**
    * Returns a substring from within this character array.
    *
    * Note that NO range checking is performed!
    */
   public String substring(int begin, int end)
   {
      return new String(buffer, begin, end - begin);
   }

   /**
    * Compares the supplied uppercase string with the contents of
    * the character array, starting at the offset specified.
    *
    * This is a specialized method to help speed up the FastPageParser
    * slightly.
    * <p/>
    * The supplied string is assumed to contain only uppercase ASCII
    * characters. The offset indicates the offset into the character
    * array that the comparison should start from.
    * <p/>
    * If (and only if) the supplied string and the relevant portion of the
    * character array are considered equal, this method will return <tt>true</tt>.
    */
   public boolean compareUpper(String upperStr, int offset)
   {
      // Range check
      if (offset < 0 || offset + upperStr.length() > size)
         return false;

      for (int i = 0; i < upperStr.length(); i++)
      {
         // & 223 converts from ASCII lowercase to ASCII uppercase
         if ((buffer[offset + i] & 223) != upperStr.charAt(i))
            return false;
      }
      return true;
   }

   /**
    * Grows the internal array by either ~100% or minSize (whichever is larger),
    * up to a maximum size of Integer.MAX_VALUE.
    */
   private final void grow(int minSize)
   {
      int newCapacity = (buffer.length + 1) * 2;
      if (newCapacity < 0)
      {
         newCapacity = Integer.MAX_VALUE;
      }
      else if (minSize > newCapacity)
      {
         newCapacity = minSize;
      }
      char newBuffer[] = new char[newCapacity];
      System.arraycopy(buffer, 0, newBuffer, 0, size);
      buffer = newBuffer;
   }
}
