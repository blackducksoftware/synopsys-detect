package com.blackducksoftware.integration.hub.detect.bomtool.npm;

import java.io.File;

public class NpmCliApplicableResult{
    private String npmExePath;
    private File packageJson;
    private File nodeModules;

    public NpmCliApplicableResult() {
    }

    public String getNpmExePath() {
        return npmExePath;
    }

    public void setNpmExePath(final String npmExePath) {
        this.npmExePath = npmExePath;
    }

    public File getPackageJson() {
        return packageJson;
    }

    public void setPackageJson(final File packageJson) {
        this.packageJson = packageJson;
    }

    public File getNodeModules() {
        return nodeModules;
    }

    public void setNodeModules(final File nodeModules) {
        this.nodeModules = nodeModules;
    }



}