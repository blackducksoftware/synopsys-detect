package com.synopsys.integration.detectable.detectables.pnpm.lockfile.model;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

public class PnpmLockYaml {
    @Nullable
    public Map<String, PnpmProjectPackage> importers;

    @Nullable
    public Map<String, String> dependencies;

    @Nullable
    public Map<String, String> devDependencies;

    @Nullable
    public Map<String, String> optionalDependencies;

    @Nullable
    public Map<String, PnpmPackageInfo> packages;

}
