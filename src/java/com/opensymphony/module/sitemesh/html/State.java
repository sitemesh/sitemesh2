package com.opensymphony.module.sitemesh.html;

import java.util.Map;
import java.util.HashMap;

public class State {

    private final Map rules = new HashMap();

    public void addRule(String tagName, TagRule rule) {
        rules.put(tagName.toLowerCase(), rule);
    }

    public boolean shouldProcessTag(String tagName) {
        return rules.containsKey(tagName.toLowerCase());
    }

    public TagRule getRule(String tagName) {
        return (TagRule) rules.get(tagName.toLowerCase());
    }
    
}
