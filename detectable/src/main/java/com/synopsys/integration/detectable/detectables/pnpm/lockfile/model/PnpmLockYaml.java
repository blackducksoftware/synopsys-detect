package com.synopsys.integration.detectable.detectables.pnpm.lockfile.model;

import java.util.Map;

public class PnpmLockYaml {
    public Map<String, String> dependencies;

    public Map<String, String> devDependencies;

    public Map<String, PnpmPackage> packages;

    public PnpmLockYaml() { }

    public Map<String, String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Map<String, String> dependencies) {
        this.dependencies = dependencies;
    }

    public Map<String, String> getDevDependencies() {
        return devDependencies;
    }

    public void setDevDependencies(Map<String, String> devDependencies) {
        this.devDependencies = devDependencies;
    }

    public Map<String, PnpmPackage> getPackages() {
        return packages;
    }

    public void setPackages(Map<String, PnpmPackage> packages) {
        this.packages = packages;
    }
}
