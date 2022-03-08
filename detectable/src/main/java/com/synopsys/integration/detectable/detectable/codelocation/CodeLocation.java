package com.synopsys.integration.detectable.detectable.codelocation;

import java.io.File;
import java.util.Optional;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class CodeLocation {
    private final File sourcePath;
    private final ExternalId externalId;
    private final DependencyGraph dependencyGraph;

    public CodeLocation(DependencyGraph dependencyGraph) {
        this(dependencyGraph, null, null);
    }

    public CodeLocation(DependencyGraph dependencyGraph, File sourcePath) {
        this(dependencyGraph, null, sourcePath);
    }

    public CodeLocation(DependencyGraph dependencyGraph, ExternalId externalId) {
        this(dependencyGraph, externalId, null);
    }

    public CodeLocation(DependencyGraph dependencyGraph, ExternalId externalId, File sourcePath) {
        this.sourcePath = sourcePath;
        this.externalId = externalId;
        this.dependencyGraph = dependencyGraph;
    }

    public Optional<File> getSourcePath() {
        return Optional.ofNullable(sourcePath);
    }

    public Optional<ExternalId> getExternalId() {
        return Optional.ofNullable(externalId);
    }

    public DependencyGraph getDependencyGraph() {
        return dependencyGraph;
    }

}
