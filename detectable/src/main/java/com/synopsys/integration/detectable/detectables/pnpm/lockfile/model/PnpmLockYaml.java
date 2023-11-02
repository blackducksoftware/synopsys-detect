package com.synopsys.integration.detectable.detectables.pnpm.lockfile.model;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

public class PnpmLockYaml {
    @Nullable
    public String lockfileVersion;
    
    @Nullable
    public Map<String, PnpmPackageInfo> packages;
}
