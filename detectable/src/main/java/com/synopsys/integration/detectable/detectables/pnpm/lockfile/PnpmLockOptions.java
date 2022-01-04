package com.synopsys.integration.detectable.detectables.pnpm.lockfile;

import java.util.List;

import com.synopsys.integration.detectable.detectable.enums.DependencyType;

public class PnpmLockOptions {
    private final List<DependencyType> allowedDependencyTypes;

    public PnpmLockOptions(List<DependencyType> allowedDependencyTypes) {
        this.allowedDependencyTypes = allowedDependencyTypes;
    }

    public List<DependencyType> getAllowedDependencyTypes() {
        return allowedDependencyTypes;
    }
}
