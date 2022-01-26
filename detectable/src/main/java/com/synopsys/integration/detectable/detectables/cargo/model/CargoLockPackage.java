package com.synopsys.integration.detectable.detectables.cargo.model;

import java.util.List;

import com.synopsys.integration.detectable.util.NameOptionalVersion;
import com.synopsys.integration.util.NameVersion;

public class CargoLockPackage {
    private final NameVersion packageNameVersion;
    private final List<NameOptionalVersion> dependencies;

    public CargoLockPackage(NameVersion packageNameVersion, List<NameOptionalVersion> dependencies) {
        this.packageNameVersion = packageNameVersion;
        this.dependencies = dependencies;
    }

    public NameVersion getPackageNameVersion() {
        return packageNameVersion;
    }

    public List<NameOptionalVersion> getDependencies() {
        return dependencies;
    }
}
