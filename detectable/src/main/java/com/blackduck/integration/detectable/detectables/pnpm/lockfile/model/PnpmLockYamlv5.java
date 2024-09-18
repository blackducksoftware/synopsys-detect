package com.blackduck.integration.detectable.detectables.pnpm.lockfile.model;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

public class PnpmLockYamlv5 extends PnpmLockYamlBase {
    @Nullable
    public Map<String, PnpmPackageInfov5> packages;
    
    @Nullable
    public Map<String, PnpmProjectPackagev5> importers;
    
    @Nullable
    public Map<String, String> dependencies;

    @Nullable
    public Map<String, String> devDependencies;

    @Nullable
    public Map<String, String> optionalDependencies;
}
