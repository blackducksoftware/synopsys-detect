package com.blackducksoftware.integration.hub.detect.bomtool;

public class BomToolSearchOptions {

    public boolean canSearchWithinApplicableDirectoryies = false;
    public int maxDepth = 1;

    public BomToolSearchOptions(final boolean canSearchWithinApplicableDirectoryies, final int maxDepth) {
        this.canSearchWithinApplicableDirectoryies = canSearchWithinApplicableDirectoryies;
        this.maxDepth = maxDepth;
    }

    public static BomToolSearchOptions defaultOptions() {
        return new BomToolSearchOptions(false, 1);
    }

    public static BomToolSearchOptions nestedBomToolDefaultOptions() {
        return new BomToolSearchOptions(false, Integer.MAX_VALUE);
    }

    public boolean canSearchWithinApplicableDirectoryies() {
        return canSearchWithinApplicableDirectoryies;
    }
}
