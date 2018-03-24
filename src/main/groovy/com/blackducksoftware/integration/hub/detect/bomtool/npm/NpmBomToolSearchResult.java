package com.blackducksoftware.integration.hub.detect.bomtool.npm;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolSearchResult;

import java.io.File;

public class NpmBomToolSearchResult extends BomToolSearchResult {
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
