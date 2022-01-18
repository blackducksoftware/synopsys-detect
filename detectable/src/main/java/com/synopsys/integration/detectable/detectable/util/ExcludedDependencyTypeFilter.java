package com.synopsys.integration.detectable.detectable.util;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

public class ExcludedDependencyTypeFilter<T extends Enum<T>> {
    private final List<T> excludedDependencyTypes;

    @SafeVarargs
    public ExcludedDependencyTypeFilter(T... excludedDependencyTypes) {
        this(Arrays.asList(excludedDependencyTypes));
    }

    public ExcludedDependencyTypeFilter(List<T> excludedDependencyTypes) {
        this.excludedDependencyTypes = excludedDependencyTypes;
    }

    public <D> void ifReportingType(T dependencyType, @Nullable D dependencies, Consumer<D> reporter) {
        if (dependencies != null && shouldReportDependencyType(dependencyType)) {
            reporter.accept(dependencies);
        }
    }

    public boolean shouldReportDependencyType(T dependencyType) {
        return !shouldExcludeDependencyType(dependencyType);
    }

    public boolean shouldExcludeDependencyType(T dependencyType) {
        return excludedDependencyTypes.contains(dependencyType);
    }
}
