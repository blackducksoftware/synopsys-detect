package com.synopsys.integration.detectable.detectables.pnpm.lockfile.model;

import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.MapUtils;
import org.jetbrains.annotations.Nullable;

public class PnpmPackageInfov6 {
    @Nullable
    public Boolean dev;
    @Nullable
    public Boolean optional;
    @Nullable
    public Map<String, String> dependencies;
    @Nullable
    public Map<String, String> optionalDependencies;
    @Nullable
    public Map<String, String> devDependencies;
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

    public Map<String, String> getDevDependencies() {
        return MapUtils.emptyIfNull(devDependencies);
    }
    
    public Map<String, String> getOptionalDependencies() {
        return MapUtils.emptyIfNull(optionalDependencies);
    }
    
    public Map<String, String> getDependencies() {
        return MapUtils.emptyIfNull(dependencies);
    }

    public Optional<PnpmDependencyType> getDependencyType() {
        if (isDev()) {
            return Optional.of(PnpmDependencyType.DEV);
        }
        if (isOptional()) {
            return Optional.of(PnpmDependencyType.OPTIONAL);
        }
        return Optional.empty();
    }

}
