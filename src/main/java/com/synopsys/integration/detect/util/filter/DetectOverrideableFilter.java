package com.synopsys.integration.detect.util.filter;

import com.synopsys.integration.util.ExcludedIncludedWildcardFilter;

public class DetectOverrideableFilter extends ExcludedIncludedWildcardFilter implements DetectFilter {
    public DetectOverrideableFilter(final String toExclude, final String toInclude) {
        super(toExclude, toInclude);
    }

    @Override
    public boolean willExclude(String itemName) {
        if (excludedSet.contains("ALL")) {
            return true;
        } else if (!excludedSet.contains("NONE") && excludedSet.contains(itemName)) {
            return true;
        } else {
            return super.willExclude(itemName);
        }
    }

    @Override
    public boolean willInclude(String itemName) {
        if (includedSet.isEmpty()) {
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
}
