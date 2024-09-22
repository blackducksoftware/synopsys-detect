package com.blackduck.integration.detectable.detectables.pnpm.lockfile.model;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

public class PnpmLockYaml extends PnpmLockYamlBase {
    @Nullable
    public Map<String, PnpmPackageInfo> packages;
    
    @Nullable
    public Map<String, PnpmProjectPackage> importers;
    
    @Nullable
    public Map<String, PnpmDependencyInfo> dependencies;

    @Nullable
    public Map<String, PnpmDependencyInfo> devDependencies;

    @Nullable
    public Map<String, PnpmDependencyInfo> optionalDependencies;
    
    @Nullable
    public Map<String, PnpmPackageInfo> snapshots;
}
