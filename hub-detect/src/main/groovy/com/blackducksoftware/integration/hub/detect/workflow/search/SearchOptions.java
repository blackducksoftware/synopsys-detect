package com.blackducksoftware.integration.hub.detect.workflow.search;

import java.io.File;
import java.util.List;

import com.synopsys.integration.util.ExcludedIncludedFilter;

public class SearchOptions {
    public File searchPath;
    public final List<String> excludedDirectories;
    public final boolean forceNestedSearch;
    public final int maxDepth;
    public final ExcludedIncludedFilter bomToolFilter;

    public SearchOptions(File searchPath, List<String> excludedDirectories, boolean forceNestedSearch, int maxDepth, ExcludedIncludedFilter bomToolFilter) {
        this.searchPath = searchPath;
        this.excludedDirectories = excludedDirectories;
        this.forceNestedSearch = forceNestedSearch;
        this.maxDepth = maxDepth;
        this.bomToolFilter = bomToolFilter;
    }
}
