package com.opensymphony.module.sitemesh.html;

import com.opensymphony.module.sitemesh.html.util.CharArray;

import java.util.ArrayList;
import java.util.List;

/**
 * A CustomTag provides a mechanism to manipulate the contents of a Tag. The standard Tag implementations
 * are immutable, however CustomTag allows a copy to be taken of an immutable Tag that can then be manipulated.
 *
 * @see Tag
 *
 * @author Joe Walnes
 */
public class CustomTag implements Tag {

    private final List attributes = new ArrayList(); // name1, value1, name2, value2...
    private String name;
    private int type;


    /**
     * Type of tag: <br/>
     * &lt;blah&gt; - Tag.OPEN<br/>
     * &lt;/blah&gt; - Tag.CLOSE<br/>
     * &lt;blah/&gt; - Tag.EMPTY<br/>
     */
    public CustomTag(String name, int type) {
        setName(name);
        setType(type);
    }

    /**
     * Create a CustomTag based on an existing Tag - this takes a copy of the Tag.
     */
    public CustomTag(Tag tag) {
        setName(tag.getName());
        setType(tag.getType());
        int c = tag.getAttributeCount();
        for (int i = 0; i < c; i++) {
            attributes.add(tag.getAttributeName(i));
            attributes.add(tag.getAttributeValue(i));
        }
    }

    public String getContents() {
        CharArray c = new CharArray(64);
        writeTo(c);
        return c.toString();
    }

    public void writeTo(CharArray out) {
        if (type == Tag.CLOSE) {
            out.append("</");
        } else {
            out.append('<');
        }

        out.append(name);
        final int len = attributes.size();

        for (int i = 0; i < len; i += 2) {
            final String name = (String) attributes.get(i);
            final String value = (String) attributes.get(i + 1);
            if (value == null) {
                out.append(' ').append(name);
            } else {
                out.append(' ').append(name).append("=\"").append(value).append("\"");
            }
        }

        if (type == Tag.EMPTY) {
            out.append("/>");
        } else {
            out.append('>');
        }
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomTag)) return false;

        final CustomTag customTag = (CustomTag) o;

        if (type != customTag.type) return false;
        if (attributes != null ? !attributes.equals(customTag.attributes) : customTag.attributes != null) return false;
        if (name != null ? !name.equals(customTag.name) : customTag.name != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (attributes != null ? attributes.hashCode() : 0);
        result = 29 * result + (name != null ? name.hashCode() : 0);
        result = 29 * result + type;
        return result;
    }

    public String toString() {
        return getContents();
    }

    // ---------- Standard methods to implement Tag interface ------

    public int getAttributeCount() {
        return attributes == null ? 0 : attributes.size() / 2;
    }

    public int getAttributeIndex(String name, boolean caseSensitive) {
        if (attributes == null) {
            return -1;
        }
        final int len = attributes.size();
        for (int i = 0; i < len; i += 2) {
            final String current = (String) attributes.get(i);
            if (caseSensitive ? name.equals(current) : name.equalsIgnoreCase(current)) {
                return i / 2;
            }
        }
        return -1;
    }

    public String getAttributeName(int index) {
        return (String) attributes.get(index * 2);
    }

    public String getAttributeValue(int index) {
        return (String) attributes.get(index * 2 + 1);
    }

    public String getAttributeValue(String name, boolean caseSensitive) {
        int attributeIndex = getAttributeIndex(name, caseSensitive);
        if (attributeIndex == -1) {
            return null;
        } else {
            return (String) attributes.get(attributeIndex * 2 + 1);
        }
    }

    public boolean hasAttribute(String name, boolean caseSensitive) {
        return getAttributeIndex(name, caseSensitive) > -1;
    }

    public String getName() {
        return name;
    }

    /**
     * Type of tag: <br/>
     * &lt;blah&gt; - Tag.OPEN<br/>
     * &lt;/blah&gt; - Tag.CLOSE<br/>
     * &lt;blah/&gt; - Tag.EMPTY<br/>
     */
    public int getType() {
        return type;
    }

    // ----------- Additional methods for changing a tag -----------

    /**
     * Change the name of the attribute.
     */
    public void setName(String name) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("CustomTag requires a name");
        } else {
            this.name = name;
        }
    }

    /**
     * Change the type of the tag.
     *
     * Type of tag: <br/>
     * &lt;blah&gt; - Tag.OPEN<br/>
     * &lt;/blah&gt; - Tag.CLOSE<br/>
     * &lt;blah/&gt; - Tag.EMPTY<br/>
     */
    public void setType(int type) {
        if (type == Tag.OPEN || type == Tag.CLOSE || type == Tag.EMPTY) {
            this.type = type;
        } else {
            throw new IllegalArgumentException("CustomTag must be of type Tag.OPEN, Tag.CLOSE or Tag.EMPTY - was " + type);
        }
    }

    /**
     * Add a new attribute. This does not check for the existence of an attribute with the same name,
     * thus allowing duplicate attributes.
     *
     * @param name           Name of attribute to change.
     * @param value          New value of attribute or null for an HTML style empty attribute.
     * @return Index of new attribute.
     */
    public int addAttribute(String name, String value) {
        attributes.add(name);
        attributes.add(value);
        return getAttributeCount() - 1;
    }

    /**
     * Change the value of an attribute, or add an attribute if it does not already exist.
     *
     * @param name           Name of attribute to change.
     * @param caseSensitive  Whether the name should be treated as case sensitive when searching for an existing value.
     * @param value          New value of attribute or null for an HTML style empty attribute.
     */
    public void setAttributeValue(String name, boolean caseSensitive, String value) {
        int attributeIndex = getAttributeIndex(name, caseSensitive);
        if (attributeIndex == -1) {
            attributes.add(name);
            attributes.add(value);
        } else {
            attributes.set(attributeIndex + 1, value);
        }
    }

    /**
     * Change the name of an existing attribute.
     */
    public void setAttributeName(int attributeIndex, String value) {
        attributes.set(attributeIndex, value);
    }

    /**
     * Change the value of an existing attribute. The value may be null for an HTML style empty attribute.
     */
    public void setAttributeValue(int attributeIndex, String value) {
        attributes.set(attributeIndex + 1, value);
    }

    /**
     * Remove an attribute.
     */
    public void removeAttribute(int attributeIndex) {
        attributes.remove(attributeIndex * 2); // name
        attributes.remove(attributeIndex * 2); // value (now shifted down a place)
    }

    /**
     * Change the value of an attribute, or add an attribute if it does not already exist.
     *
     * @param name           Name of attribute to remove.
     * @param caseSensitive  Whether the name should be treated as case sensitive.
     */
    public void removeAttribute(String name, boolean caseSensitive) {
        int attributeIndex = getAttributeIndex(name, caseSensitive);
        if (attributeIndex == -1) {
            throw new IllegalArgumentException("Attribute " + name + " not found");
        } else {
            attributes.remove(attributeIndex * 2); // name
            attributes.remove(attributeIndex * 2); // value (now shifted down a place)
        }
    }
}
