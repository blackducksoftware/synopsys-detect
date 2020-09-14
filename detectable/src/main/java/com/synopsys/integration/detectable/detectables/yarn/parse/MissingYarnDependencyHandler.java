package com.synopsys.integration.detectable.detectables.yarn.parse;

import org.slf4j.Logger;

import com.synopsys.integration.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder;
import com.synopsys.integration.bdio.model.dependencyid.DependencyId;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

@FunctionalInterface
public interface MissingYarnDependencyHandler {
    ExternalId handleMissingYarnDependency(Logger logger, ExternalIdFactory externalIdFactory, DependencyId dependencyId, LazyExternalIdDependencyGraphBuilder.LazyDependencyInfo lazyDependencyInfo, String yarnLockFilePath);
}
