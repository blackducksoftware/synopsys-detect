package com.synopsys.integration.detectable.detectables.pnpm.lockfile.model;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

public class PnpmLockYamlv6 {
    @Nullable
    public Map<String, PnpmProjectPackagev6> importers;
    
    @Nullable
    // TODO can hopefully reuse
    public Map<String, PnpmPackageInfo> packages;
}
