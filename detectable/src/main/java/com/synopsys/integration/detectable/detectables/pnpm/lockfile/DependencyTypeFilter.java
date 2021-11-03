/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.pnpm.lockfile;

import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detectable.detectable.enums.DependencyType;

public class DependencyTypeFilter {
    private final List<DependencyType> dependencyTypes;

    public DependencyTypeFilter(List<DependencyType> dependencyTypes) {
        this.dependencyTypes = dependencyTypes;
    }

    public <T> void ifReportingType(DependencyType dependencyType, @Nullable T dependency, Consumer<T> reporter) {
        if (dependency != null && shouldReportDependencyType(dependencyType)) {
            reporter.accept(dependency);
        }
    }

    public boolean shouldReportDependencyType(DependencyType dependencyType) {
        return dependencyTypes.contains(dependencyType);
    }
}
