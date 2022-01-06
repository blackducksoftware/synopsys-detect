package com.synopsys.integration.detectable.detectables.pnpm.lockfile.model;

import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.jetbrains.annotations.Nullable;

public class PnpmPackageInfo {
    @Nullable
    public Boolean dev;
    @Nullable
    public Boolean optional;
    @Nullable
    public Map<String, String> dependencies;
    @Nullable
    public String name;
    @Nullable
    public String version;

    private boolean isDev() {
        return dev != null && dev;
    }

    private boolean isOptional() {
        return optional != null && optional;
    }

    public Map<String, String> getDependencies() {
        return MapUtils.emptyIfNull(dependencies);
    }

    public PnpmDependencyType getDependencyType() {
        if (isDev()) {
            return PnpmDependencyType.DEV;
        }
        if (isOptional()) {
            return PnpmDependencyType.OPTIONAL;
        }
        return PnpmDependencyType.APP;
    }

}
