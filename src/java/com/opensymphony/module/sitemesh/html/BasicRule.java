package com.opensymphony.module.sitemesh.html;

public abstract class BasicRule implements TagRule {

    protected HTMLProcessorContext context;

    public void setContext(HTMLProcessorContext context) {
        this.context = context;
    }

    public abstract void process(Tag tag);

}
