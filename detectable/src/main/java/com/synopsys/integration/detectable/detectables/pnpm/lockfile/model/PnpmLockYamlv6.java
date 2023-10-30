package com.synopsys.integration.detectable.detectables.pnpm.lockfile.model;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

public class PnpmLockYamlv6 {
    @Nullable
    public Map<String, PnpmProjectPackagev6> importers;
    
    @Nullable
    public Map<String, PnpmDependencyInfo> dependencies;

    @Nullable
    public Map<String, PnpmDependencyInfo> devDependencies;

    @Nullable
    public Map<String, PnpmDependencyInfo> optionalDependencies;
    
    @Nullable
    public Map<String, PnpmPackageInfo> packages;
    
}
