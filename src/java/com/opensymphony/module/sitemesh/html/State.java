package com.opensymphony.module.sitemesh.html;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

public class State {

    private final List rules = new ArrayList();

    public void addRule(TagRule rule) {
        rules.add(rule);
    }

    public boolean shouldProcessTag(String tagName) {
        for (Iterator iterator = rules.iterator(); iterator.hasNext();) {
            TagRule tagRule = (TagRule) iterator.next();
            if (tagRule.shouldProcess(tagName)) {
                return true;
            }
        }
        return false;
    }

    public TagRule getRule(String tagName) {
        for (Iterator iterator = rules.iterator(); iterator.hasNext();) {
            TagRule tagRule = (TagRule) iterator.next();
            if (tagRule.shouldProcess(tagName)) {
                return tagRule;
            }
        }
        return null;
    }
    
}
