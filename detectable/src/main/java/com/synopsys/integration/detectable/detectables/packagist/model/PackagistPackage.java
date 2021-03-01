/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.packagist.model;

import java.util.List;

import com.synopsys.integration.util.NameVersion;

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
