package com.blackducksoftware.integration.hub.detect.bomtool.search;

import java.io.File;

public class BomToolSearchResult {
    private final boolean applicable;
    private File searchedDirectory;

    public BomToolSearchResult(final boolean applicable, File searchedDirectory) {
        this.applicable = applicable;
        this.searchedDirectory = searchedDirectory;
    }

    public boolean isApplicable() {
        return applicable;
    }

    public File getSearchedDirectory() {
        return searchedDirectory;
    }

}
