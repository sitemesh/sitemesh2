package com.opensymphony.module.sitemesh.html;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class State {

    private TagRule[] rules = new TagRule[16]; // List is too slow, according to profiler
    private int ruleCount = 0;
    private final ArrayList listeners = new ArrayList();

    public void addRule(TagRule rule) {
        if (ruleCount == rules.length) {
            TagRule[] longerArray = new TagRule[rules.length * 2];
            System.arraycopy(rules, 0, longerArray, 0, ruleCount);
            rules = longerArray;
        }
        rules[ruleCount++] = rule;
    }

    public boolean shouldProcessTag(String tagName) {
        for (int i = 0; i < ruleCount; i++) {
            if (rules[i].shouldProcess(tagName)) {
                return true;
            }
        }
        return false;
    }

    public TagRule getRule(String tagName) {
        for (int i = 0; i < ruleCount; i++) {
            if (rules[i].shouldProcess(tagName)) {
                return rules[i];
            }
        }
        return null;
    }

	public void addListener(StateChangeListener listener) {
		listeners.add(listener);
	}

	public void endOfState() {
		for (Iterator iter = listeners.iterator(); iter.hasNext();) {
			StateChangeListener listener = (StateChangeListener) iter.next();
			listener.stateFinished();			
		}
	}
    
}
