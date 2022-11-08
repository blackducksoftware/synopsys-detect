package com.synopsys.integration.detectable.detectables.swift.lock.model;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.detectable.result.FailedDetectableResult;

public class PackageResolvedResult {
    private final DependencyGraph dependencyGraph;
    @Nullable
    private final FailedDetectableResult failedDetectableResult;

    public static PackageResolvedResult failure(FailedDetectableResult failedDetectableResult) {
        return new PackageResolvedResult(new BasicDependencyGraph(), failedDetectableResult);
    }

    public static PackageResolvedResult empty() {
        return new PackageResolvedResult(new BasicDependencyGraph(), null);
    }

    public static PackageResolvedResult success(DependencyGraph dependencyGraph) {
        return new PackageResolvedResult(dependencyGraph, null);
    }

    private PackageResolvedResult(DependencyGraph dependencyGraph, @Nullable FailedDetectableResult failedDetectableResult) {
        this.dependencyGraph = dependencyGraph;
        this.failedDetectableResult = failedDetectableResult;
    }

    public DependencyGraph getDependencyGraph() {
        return dependencyGraph;
    }

    public Optional<FailedDetectableResult> getFailedDetectableResult() {
        return Optional.ofNullable(failedDetectableResult);
    }

    public boolean isFailure() {
        return getFailedDetectableResult().isPresent();
    }
}
