package com.synopsys.integration.detectable.detectables.sbt.dot;

import java.util.Collections;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SbtProjectMatch {
    @NotNull
    private final Set<String> nodeIdsAtRoot;
    @Nullable
    private SbtProject relatedProject;

    private SbtProjectMatch(@NotNull final Set<String> nodeIds, @Nullable final SbtProject relatedProject) {
        this.nodeIdsAtRoot = nodeIds;
        this.relatedProject = relatedProject;
    }

    public static SbtProjectMatch FoundMatch(@NotNull final String nodeId, @NotNull final SbtProject relatedProject) {
        return new SbtProjectMatch(Collections.singleton(nodeId), relatedProject);
    }

    public static SbtProjectMatch NoMatch(Set<String> rootNodes) {
        return new SbtProjectMatch(rootNodes, null);
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
