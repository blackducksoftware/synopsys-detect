package com.synopsys.integration.detect.util.filter;

import java.util.Set;

import com.synopsys.integration.detect.configuration.help.DetectArgumentState;
import com.synopsys.integration.util.ExcludedIncludedWildcardFilter;
import com.synopsys.integration.util.TokenizerUtils;

public class DetectOverrideableFilter extends ExcludedIncludedWildcardFilter implements DetectFilter {
    public static DetectOverrideableFilter createArgumentValueFilter(DetectArgumentState detectArgumentState) {
        return new DetectOverrideableFilter("", detectArgumentState.getParsedValue());
    }

    public DetectOverrideableFilter(String toExclude, String toInclude) {
        super(TokenizerUtils.createSetFromString(toExclude), TokenizerUtils.createSetFromString(toInclude));
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
