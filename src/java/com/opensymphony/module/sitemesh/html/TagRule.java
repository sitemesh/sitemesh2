package com.opensymphony.module.sitemesh.html;

public interface TagRule {
    void setContext(HTMLProcessorContext context);
    boolean shouldProcess(String name);
    void process(Tag tag);
}
