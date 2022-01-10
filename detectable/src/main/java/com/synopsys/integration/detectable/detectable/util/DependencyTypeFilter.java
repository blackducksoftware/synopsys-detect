package com.synopsys.integration.detectable.detectable.util;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

/**
 * @deprecated Please use the ExcludedDependencyTypeFilter instead.
 */
@Deprecated
public class DependencyTypeFilter<T extends Enum<T>> {
    private final List<T> allowedDependencyTypes;

    @SafeVarargs
    public DependencyTypeFilter(T... allowedDependencyTypes) {
        this(Arrays.asList(allowedDependencyTypes));
    }

    public DependencyTypeFilter(List<T> allowedDependencyTypes) {
        this.allowedDependencyTypes = allowedDependencyTypes;
    }

    public <D> void ifReportingType(T dependencyType, @Nullable D dependencies, Consumer<D> reporter) {
        if (dependencies != null && shouldReportDependencyType(dependencyType)) {
            reporter.accept(dependencies);
        }
    }

    public boolean shouldReportDependencyType(T dependencyType) {
        return allowedDependencyTypes.contains(dependencyType);
    }
}
