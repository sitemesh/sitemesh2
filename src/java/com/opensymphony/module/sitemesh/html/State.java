package com.opensymphony.module.sitemesh.html;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class State {

    private final List rules = new ArrayList();
    private final ArrayList listeners = new ArrayList();

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
