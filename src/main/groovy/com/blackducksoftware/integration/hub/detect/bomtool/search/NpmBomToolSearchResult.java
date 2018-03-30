package com.blackducksoftware.integration.hub.detect.bomtool.search;

import java.io.File;

public class NpmBomToolSearchResult extends BomToolSearchResult {
    public static final NpmBomToolSearchResult NPM_BOM_TOOL_DOES_NOT_APPLY = new NpmBomToolSearchResult(false, null, null, null);

    private final String npmExePath;
    private final File packageLockJson;
    private final File shrinkwrapJson;

    public NpmBomToolSearchResult(final boolean applicable, final String npmExePath, final File packageLockJson, final File shrinkwrapJson) {
        super(applicable);
        this.npmExePath = npmExePath;
        this.packageLockJson = packageLockJson;
        this.shrinkwrapJson = shrinkwrapJson;
    }

    public String getNpmExePath() {
        return npmExePath;
    }

    public File getPackageLockJson() {
        return packageLockJson;
    }

    public File getShrinkwrapJson() {
        return shrinkwrapJson;
    }

}
