package com.synopsys.integration.detectable.detectables.xcode.model;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.detectable.detectable.result.FailedDetectableResult;

public class XcodeWorkspaceResult {
    @Nullable
    private final BasicDependencyGraph dependencyGraph;
    private final List<FailedDetectableResult> failedDetectableResults;

    public static XcodeWorkspaceResult failure(List<FailedDetectableResult> failedDetectableResults) {
        return new XcodeWorkspaceResult(null, failedDetectableResults);
    }

    public static XcodeWorkspaceResult success(List<BasicDependencyGraph> dependencyGraphs) {
        BasicDependencyGraph xcodeWorkspaceGraph = new BasicDependencyGraph();
        dependencyGraphs.forEach(xcodeWorkspaceGraph::copyGraphToRoot);
        return new XcodeWorkspaceResult(xcodeWorkspaceGraph, Collections.emptyList());
    }

    private XcodeWorkspaceResult(@Nullable BasicDependencyGraph dependencyGraph, List<FailedDetectableResult> failedDetectableResults) {
        this.dependencyGraph = dependencyGraph;
        this.failedDetectableResults = failedDetectableResults;
    }

    @Nullable
    public BasicDependencyGraph getDependencyGraph() {
        return dependencyGraph;
    }

    public List<FailedDetectableResult> getFailedDetectableResults() {
        return failedDetectableResults;
    }

    public boolean isFailure() {
        return CollectionUtils.isNotEmpty(getFailedDetectableResults());
    }
}
