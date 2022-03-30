package com.synopsys.integration.detectable.detectables.xcode.model;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.detectable.detectable.result.FailedDetectableResult;

public class XcodeWorkspaceResult {
    @Nullable
    private final DependencyGraph dependencyGraph;
    private final List<FailedDetectableResult> failedDetectableResults;

    public static XcodeWorkspaceResult failure(List<FailedDetectableResult> failedDetectableResults) {
        return new XcodeWorkspaceResult(null, failedDetectableResults);
    }

    public static XcodeWorkspaceResult fromGraphs(List<DependencyGraph> dependencyGraphs) {
        MutableMapDependencyGraph xcodeWorkspaceGraph = new MutableMapDependencyGraph();
        dependencyGraphs.forEach(xcodeWorkspaceGraph::addGraphAsChildrenToRoot);
        return new XcodeWorkspaceResult(xcodeWorkspaceGraph, Collections.emptyList());
    }

    private XcodeWorkspaceResult(@Nullable DependencyGraph dependencyGraph, List<FailedDetectableResult> failedDetectableResults) {
        this.dependencyGraph = dependencyGraph;
        this.failedDetectableResults = failedDetectableResults;
    }

    @Nullable
    public DependencyGraph getDependencyGraph() {
        return dependencyGraph;
    }

    public List<FailedDetectableResult> getFailedDetectableResults() {
        return failedDetectableResults;
    }

    public boolean isFailure() {
        return CollectionUtils.isNotEmpty(getFailedDetectableResults());
    }
}
