package com.opensymphony.module.sitemesh.html;

public class StateTransitionRule extends BasicRule {

    private final State newState;
    private final boolean includeEnclosingTags;

    private State lastState;

    public StateTransitionRule(String tagName, State newState) {
        this(tagName, newState, true);
    }

    public StateTransitionRule(String tagName, State newState, boolean includeEnclosingTags) {
        super(tagName);
        this.newState = newState;
        this.includeEnclosingTags = includeEnclosingTags;
    }

    public void process(Tag tag) {
        if (tag.getType() == Tag.OPEN) {
            lastState = context.currentState();
            context.changeState(newState);
            newState.addRule(this);
        } else if (tag.getType() == Tag.CLOSE && lastState != null) {
            context.changeState(lastState);
            lastState = null;
        }
        if (includeEnclosingTags) {
            tag.writeTo(context.currentBuffer());
        }
    }
}
