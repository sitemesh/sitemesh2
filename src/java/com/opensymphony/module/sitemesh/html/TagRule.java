package com.opensymphony.module.sitemesh.html;

public interface TagRule {
    void setContext(HTMLProcessorContext context);
    void process(Tag tag);
}
