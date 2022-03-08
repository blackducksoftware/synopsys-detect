package com.synopsys.integration.detectable.detectables.pnpm.lockfile.model;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

public class PnpmProjectPackage {
    @Nullable
    public Map<String, String> dependencies;

    @Nullable
    public Map<String, String> devDependencies;

    @Nullable
    public Map<String, String> optionalDependencies;
}
