package com.blackduck.integration.detectable.detectables.pnpm.lockfile.model;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

public class PnpmProjectPackagev5 {
    @Nullable
    public Map<String, String> dependencies;

    @Nullable
    public Map<String, String> devDependencies;

    @Nullable
    public Map<String, String> optionalDependencies;
}
