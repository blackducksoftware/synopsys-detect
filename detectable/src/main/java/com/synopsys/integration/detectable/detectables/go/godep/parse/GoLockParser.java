/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
