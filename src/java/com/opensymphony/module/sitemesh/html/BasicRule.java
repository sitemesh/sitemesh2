package com.opensymphony.module.sitemesh.html;

public abstract class BasicRule implements TagRule {

    private final String acceptableTagName;

    protected HTMLProcessorContext context;

    protected BasicRule(String acceptableTagName) {
        this.acceptableTagName = acceptableTagName;
    }

    protected BasicRule() {
        this.acceptableTagName = null;
    }

    public void setContext(HTMLProcessorContext context) {
        this.context = context;
    }

    public boolean shouldProcess(String name) {
        if (acceptableTagName == null) {
            throw new UnsupportedOperationException(getClass().getName()
                    + " should be constructed with acceptableTagName OR should implement shouldProcess()");
        }
        return name.toLowerCase().equals(acceptableTagName);
    }

    public abstract void process(Tag tag);

}
