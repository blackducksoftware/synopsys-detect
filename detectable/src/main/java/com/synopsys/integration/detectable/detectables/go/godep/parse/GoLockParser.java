/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.go.godep.parse;

import java.io.InputStream;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.moandjiezana.toml.Toml;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.go.godep.model.GoLock;
import com.synopsys.integration.detectable.detectables.go.godep.model.Project;

public class GoLockParser {
    private final ExternalIdFactory externalIdFactory;

    public GoLockParser(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph parseDepLock(final InputStream depLockInputStream) {
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();
        final GoLock goLock = new Toml().read(depLockInputStream).to(GoLock.class);
        if (goLock.projects != null) {
            for (final Project project : goLock.projects) {
                if (project != null) {
                    final String projectName = project.getName();
                    final String projectVersion = Optional.ofNullable(StringUtils.stripToNull(project.getVersion())).orElse(project.getRevision());
                    project.getPackages().stream()
                        .map(packageName -> createDependencyName(projectName, packageName))
                        .map(dependencyName -> createGoDependency(dependencyName, projectVersion))
                        .forEach(graph::addChildToRoot);
                }
            }
        }
        return graph;
    }

    private String createDependencyName(final String projectName, final String parsedPackageName) {
        String dependencyName = projectName;

        if (!".".equals(parsedPackageName)) {
            dependencyName = dependencyName + "/" + parsedPackageName;
        }
        if (dependencyName.startsWith("golang.org/x/")) {
            dependencyName = dependencyName.replace("golang.org/x/", "");
        }

        return dependencyName;
    }

    private Dependency createGoDependency(final String name, final String version) {
        final ExternalId dependencyExternalId = externalIdFactory.createNameVersionExternalId(Forge.GOLANG, name, version);
        return new Dependency(name, version, dependencyExternalId);
    }

}
