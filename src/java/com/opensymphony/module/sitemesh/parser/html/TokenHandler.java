package com.opensymphony.module.sitemesh.parser.html;

public interface TokenHandler {
    void tag(Tag tag);
    void text(Text text);
    void error(String message, int line, int column);
}
