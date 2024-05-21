package com.opensymphony.module.sitemesh.html;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class State {

    private TagRule[] rules = new TagRule[16]; // List is too slow, according to profiler
    private int ruleCount = 0;
    private List listeners = null;
    private List textFilters = null; // lazily instantiated to reduce overhead for most cases where it's not needed.

    public void addRule(TagRule rule) {
        if (ruleCount == rules.length) {
            // grow array if necessary
            TagRule[] longerArray = new TagRule[rules.length * 2];
            System.arraycopy(rules, 0, longerArray, 0, ruleCount);
            rules = longerArray;
        }
        rules[ruleCount++] = rule;
    }

    public void addTextFilter(TextFilter textFilter) {
        if (textFilters == null) {
            textFilters = new ArrayList(); // lazy instantiation
        }
        textFilters.add(textFilter);
    }

    public boolean shouldProcessTag(String tagName) {
        for (int i = ruleCount - 1; i >= 0; i--) { // reverse iteration to so most recently added rule matches
            if (rules[i].shouldProcess(tagName)) {
                return true;
            }
        }
        return false;
    }

    public TagRule getRule(String tagName) {
        for (int i = ruleCount - 1; i >= 0; i--) { // reverse iteration to so most recently added rule matches
            if (rules[i].shouldProcess(tagName)) {
                return rules[i];
            }
        }
        return null;
    }

	public void addListener(StateChangeListener listener) {
    if(listeners == null) listeners = new ArrayList();
    listeners.add(listener);
	}

	public void endOfState() {
    if(listeners == null) return;
    for (Iterator iter = listeners.iterator(); iter.hasNext();) {
			StateChangeListener listener = (StateChangeListener) iter.next();
			listener.stateFinished();			
		}
	}

    public void handleText(Text text, HTMLProcessorContext context) {
        if (textFilters == null) {
            text.writeTo(context.currentBuffer());
        } else {
            String asString = text.getContents();
            for (Iterator iterator = textFilters.iterator(); iterator.hasNext();) {
                TextFilter textFilter = (TextFilter) iterator.next();
                asString = textFilter.filter(asString);
            }
            context.currentBuffer().append(asString);
        }
    }

}
