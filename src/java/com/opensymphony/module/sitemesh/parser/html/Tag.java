package com.opensymphony.module.sitemesh.parser.html;

public interface Tag {
    int UNKNOWN = 0;
    int OPEN = 1;
    int CLOSE = 2;
    int EMPTY = 3;

    String getCompleteTag();

    String getName();
    int getType();

    int getAttributeCount();
    String getAttributeName(int index);
    String getAttributeValue(int index);
    String getAttributeValue(String name);
}
