package com.blackducksoftware.integration.hub.detect.bomtool.search;

import java.io.File;

public class BomToolSearchResultFactory {
    public static BomToolSearchResult createApplies(File searchedDirectory) {
        return new BomToolSearchResult(true, searchedDirectory);
    }

    public static BomToolSearchResult createDoesNotApply() {
        return new BomToolSearchResult(false, null);
    }

    public static NpmBomToolSearchResult createNpmApplies(final File searchedDirectory, final String npmExePath, final File packageLockJson, final File shrinkwrapJson) {
        return new NpmBomToolSearchResult(true, searchedDirectory, npmExePath, packageLockJson, shrinkwrapJson);
    }

    public static NpmBomToolSearchResult createNpmDoesNotApply() {
        return new NpmBomToolSearchResult(false, null, null, null, null);
    }

}
