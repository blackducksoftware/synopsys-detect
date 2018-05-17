package com.blackducksoftware.integration.hub.detect.bomtool.search;

import java.util.List;

import com.blackducksoftware.integration.util.ExcludedIncludedFilter;

public class BomToolFinderOptions {

    private final List<String> excludedDirectories;
    private final Boolean forceNestedSearch;
    private final int maximumDepth;
    private final ExcludedIncludedFilter bomToolFilter;

    public BomToolFinderOptions(final List<String> excludedDirectories, final Boolean forceNestedSearch, final int maximumDepth, final ExcludedIncludedFilter bomToolFilter) {
        this.excludedDirectories = excludedDirectories;
        this.forceNestedSearch = forceNestedSearch;
        this.maximumDepth = maximumDepth;
        this.bomToolFilter = bomToolFilter;
    }

    public List<String> getExcludedDirectories() {
        return excludedDirectories;
    }

    public Boolean getForceNestedSearch() {
        return forceNestedSearch;
    }

    public ExcludedIncludedFilter getBomToolFilter() {
        return bomToolFilter;
    }

    public int getMaximumDepth() {
        return maximumDepth;
    }

}
