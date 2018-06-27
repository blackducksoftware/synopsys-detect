package com.blackducksoftware.integration.hub.detect.extraction.bomtool.packagist.parse.model;

import java.util.List;

import com.blackducksoftware.integration.util.NameVersion;

public class PackagistPackage {
    private final NameVersion nameVersion;
    private final List<NameVersion> dependencies;

    public PackagistPackage(final NameVersion nameVersion, final List<NameVersion> dependencies) {
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
