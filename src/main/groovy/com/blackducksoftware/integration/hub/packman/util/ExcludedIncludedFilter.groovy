package com.blackducksoftware.integration.hub.packman.util

import org.apache.commons.lang3.StringUtils

public class ExcludedIncludedFilter {
    private Set<String> excludedSet
    private Set<String> includedSet

    /**
     * Provide a comma-separated list of names to exclude and/or a comma-separated list of names to include. Exclusion rules always win.
     */
    public ExcludedIncludedFilter(final String toExclude, final String toInclude) {
        excludedSet = createSetFromString(toExclude)
        includedSet = createSetFromString(toInclude)
    }

    public boolean shouldInclude(final String itemName) {
        if (excludedSet.contains(itemName)) {
            return false
        }

        if (includedSet.size() > 0 && !includedSet.contains(itemName)) {
            return false
        }

        return true
    }

    private Set<String> createSetFromString(final String s) {
        new HashSet<String>(StringUtils.trimToEmpty(s).tokenize(',').collect { it.trim() })
    }
}
