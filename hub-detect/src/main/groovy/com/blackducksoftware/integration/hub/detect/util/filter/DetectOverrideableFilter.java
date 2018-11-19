package com.blackducksoftware.integration.hub.detect.util.filter;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

public class DetectOverrideableFilter implements DetectFilter {
    private final Set<String> excludedSet;
    private final Set<String> includedSet;

    public DetectOverrideableFilter(final String toExclude, final String toInclude) {
        excludedSet = createSetFromString(toExclude);
        includedSet = createSetFromString(toInclude);
    }

    public boolean shouldInclude(final String itemName) {
        if (excludedSet.contains("ALL"))
            return false;

        if (!excludedSet.contains("NONE") && excludedSet.contains(itemName)) {
            return false;
        }

        if (includedSet.size() > 0) {
            if (includedSet.contains("ALL")) {
                return true;
            } else if (includedSet.contains("NONE")) {
                return false;
            } else if (!includedSet.contains(itemName)) {
                return false;
            }
        }

        return true;
    }

    private Set<String> createSetFromString(final String s) {
        final Set<String> set = new HashSet<>();
        final StringTokenizer stringTokenizer = new StringTokenizer(StringUtils.trimToEmpty(s), ",");
        while (stringTokenizer.hasMoreTokens()) {
            set.add(StringUtils.trimToEmpty(stringTokenizer.nextToken()));
        }
        return set;
    }
}
