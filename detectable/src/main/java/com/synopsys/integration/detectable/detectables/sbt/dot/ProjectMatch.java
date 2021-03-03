package com.synopsys.integration.detectable.detectables.sbt.dot;

import java.util.Collections;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProjectMatch {
    @NotNull
    private final Set<String> nodeIdsAtRoot;
    @Nullable
    private SbtProject relatedProject;

    private ProjectMatch(@NotNull final Set<String> nodeIds, @Nullable final SbtProject relatedProject) {
        this.nodeIdsAtRoot = nodeIds;
        this.relatedProject = relatedProject;
    }

    public static ProjectMatch FoundMatch(@NotNull final String nodeId, @NotNull final SbtProject relatedProject) {
        return new ProjectMatch(Collections.singleton(nodeId), relatedProject);
    }

    public static ProjectMatch NoMatch(Set<String> rootNodes) {
        return new ProjectMatch(rootNodes, null);
    }

    @NotNull
    public Set<String> getNodeIdsAtRoot() {
        return nodeIdsAtRoot;
    }

    @Nullable
    public SbtProject getRelatedProject() {
        return relatedProject;
    }
}
