package com.synopsys.integration.detect.util.filter;

import java.util.Set;

import com.synopsys.integration.util.ExcludedIncludedWildcardFilter;

public class DetectOverrideableFilter extends ExcludedIncludedWildcardFilter implements DetectFilter {
    public DetectOverrideableFilter(final String toExclude, final String toInclude) {
        super(toExclude, toInclude);
    }

    @Override
    public boolean willExclude(final String itemName) {
        if (excludedSet.contains("ALL")) {
            return true;
        } else if (!excludedSet.contains("NONE") && excludedSet.contains(itemName)) {
            return true;
        } else {
            return super.willExclude(itemName);
        }
    }

    @Override
    public boolean willInclude(final String itemName) {
        if (!includedSet.isEmpty()) {
            if (includedSet.contains("ALL")) {
                return true;
            } else if (includedSet.contains("NONE")) {
                return false;
            } else {
                return includedSet.contains(itemName);
            }
        }

        return super.willInclude(itemName);
    }

    // TODO - edit ExcludedIncludedFilter to have getters for includedSet, excludedSet
    public Set<String> getIncludedSet() {
        return includedSet;
    }
}
