/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.codelocation;

import java.io.File;
import java.util.Optional;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class CodeLocation {
    private final File sourcePath;
    private final ExternalId externalId;
    private final DependencyGraph dependencyGraph;

    public CodeLocation(final DependencyGraph dependencyGraph) {
        this(dependencyGraph, null, null);
    }

    public CodeLocation(final DependencyGraph dependencyGraph, final File sourcePath) {
        this(dependencyGraph, null, sourcePath);
    }

    public CodeLocation(final DependencyGraph dependencyGraph, final ExternalId externalId) {
        this(dependencyGraph, externalId, null);
    }

    public CodeLocation(final DependencyGraph dependencyGraph, final ExternalId externalId, final File sourcePath) {
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
