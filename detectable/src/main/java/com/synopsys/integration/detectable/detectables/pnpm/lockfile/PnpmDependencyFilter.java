/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.pnpm.lockfile;

import java.util.List;

import com.synopsys.integration.detectable.detectable.enums.DependencyType;

public class PnpmDependencyFilter {
    private List<DependencyType> dependencyTypes;

    public PnpmDependencyFilter(List<DependencyType> dependencyTypes) {
        this.dependencyTypes = dependencyTypes;
    }

    public boolean shouldReportDependencies() {
        return dependencyTypes.contains(DependencyType.APP);
    }

    public boolean shouldReportDevDependencies() {
        return dependencyTypes.contains(DependencyType.DEV);
    }

    public boolean shouldReportOptionalDependencies() {
        return dependencyTypes.contains(DependencyType.OPTIONAL);
    }

    public boolean shouldReportDependencyType(DependencyType dependencyType) {
        return dependencyTypes.contains(dependencyType);
    }
}
