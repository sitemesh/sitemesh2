package com.opensymphony.module.sitemesh.html;

public interface TagRule {
    void setContext(HTMLProcessorContext context);

    /**
     * Called by the HTMLProcessor to determine if a rule should be called for a given tag.
     *
     * The name parameter will always be passed in lowercase. 
     */
    boolean shouldProcess(String name);

    void process(Tag tag);
}
