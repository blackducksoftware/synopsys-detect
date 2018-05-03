package com.blackducksoftware.integration.hub.detect.bomtool.search;

public class BomToolSearchOptions {

    public boolean canSearchWithinApplicableDirectories = false;
    public int maxDepth = 1;

    public BomToolSearchOptions(final boolean canSearchWithinApplicableDirectories, final int maxDepth) {
        this.canSearchWithinApplicableDirectories = canSearchWithinApplicableDirectories;
        this.maxDepth = maxDepth;
    }

    public static BomToolSearchOptions defaultOptions() {
        return new BomToolSearchOptions(false, 1);
    }

    public static BomToolSearchOptions nestedBomToolDefaultOptions() {
        return new BomToolSearchOptions(false, Integer.MAX_VALUE);
    }

    public boolean canSearchWithinApplicableDirectories() {
        return canSearchWithinApplicableDirectories;
    }
}
