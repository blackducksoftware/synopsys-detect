package com.blackducksoftware.integration.hub.detect.bomtool.search;

import java.util.List;

public class BomToolFinderOptions {

    private final List<String> excludedDirectories;
    private final Boolean forceNestedSearch;
    private final int maximumDepth;

    public BomToolFinderOptions(final List<String> excludedDirectories, final Boolean forceNestedSearch, final int maximumDepth) {
        this.excludedDirectories = excludedDirectories;
        this.forceNestedSearch = forceNestedSearch;
        this.maximumDepth = maximumDepth;
    }

    public List<String> getExcludedDirectories() {
        return excludedDirectories;
    }

    public Boolean getForceNestedSearch() {
        return forceNestedSearch;
    }

    public int getMaximumDepth() {
        return maximumDepth;
    }

}
