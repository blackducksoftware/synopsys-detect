package com.synopsys.integration.detectable.detectables.xcode.model;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.ProjectDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.ProjectDependency;
import com.synopsys.integration.detectable.detectable.result.FailedDetectableResult;
import com.synopsys.integration.detectable.detectables.swift.cli.SwiftPackageTransformer;

public class XcodeWorkspaceResult {
    @Nullable
    private final ProjectDependencyGraph dependencyGraph;
    private final List<FailedDetectableResult> failedDetectableResults;

    public static XcodeWorkspaceResult failure(List<FailedDetectableResult> failedDetectableResults) {
        return new XcodeWorkspaceResult(null, failedDetectableResults);
    }

    public static XcodeWorkspaceResult success(List<DependencyGraph> dependencyGraphs, Path discoveryLocation) {
        ProjectDependencyGraph xcodeWorkspaceGraph = new ProjectDependencyGraph(new ProjectDependency(SwiftPackageTransformer.SWIFT_FORGE, discoveryLocation));
        dependencyGraphs.forEach(xcodeWorkspaceGraph::copyGraphToRoot);
        return new XcodeWorkspaceResult(xcodeWorkspaceGraph, Collections.emptyList());
    }

    private XcodeWorkspaceResult(@Nullable ProjectDependencyGraph dependencyGraph, List<FailedDetectableResult> failedDetectableResults) {
        this.dependencyGraph = dependencyGraph;
        this.failedDetectableResults = failedDetectableResults;
    }

    @Nullable
    public ProjectDependencyGraph getDependencyGraph() {
        return dependencyGraph;
    }

    public List<FailedDetectableResult> getFailedDetectableResults() {
        return failedDetectableResults;
    }

    public boolean isFailure() {
        return CollectionUtils.isNotEmpty(getFailedDetectableResults());
    }
}
