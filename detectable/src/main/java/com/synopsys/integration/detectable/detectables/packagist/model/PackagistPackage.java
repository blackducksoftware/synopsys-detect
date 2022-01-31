package com.synopsys.integration.detectable.detectables.packagist.model;

import java.util.List;

import com.synopsys.integration.util.NameVersion;

public class PackagistPackage {
    private final NameVersion nameVersion;
    private final List<NameVersion> dependencies;

    public PackagistPackage(NameVersion nameVersion, List<NameVersion> dependencies) {
        this.nameVersion = nameVersion;
        this.dependencies = dependencies;
    }

    public NameVersion getNameVersion() {
        return nameVersion;
    }

    public List<NameVersion> getDependencies() {
        return dependencies;
    }
}
