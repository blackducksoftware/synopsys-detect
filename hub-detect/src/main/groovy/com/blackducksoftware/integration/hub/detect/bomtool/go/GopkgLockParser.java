/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.bomtool.go;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.moandjiezana.toml.Toml;

public class GopkgLockParser {
    public ExternalIdFactory externalIdFactory;

    public GopkgLockParser(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph parseDepLock(final String depLockContents) {
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();
        final GopkgLock gopkgLock = new Toml().read(depLockContents).to(GopkgLock.class);

        for (final Project project : gopkgLock.getProjects()) {
            if (project != null) {
                final String name = project.getName();
                String version = "";
                if (StringUtils.isNotBlank(project.getVersion())) {
                    version = project.getVersion();
                } else {
                    version = project.getRevision();
                }
                for (final String pkg : project.getPackages()) {
                    String packageName = name;
                    if (!pkg.equals(".")) {
                        packageName = packageName + "/" + pkg;
                    }
                    if (packageName.startsWith("golang.org/x/")) {
                        packageName = packageName.replaceAll("golang.org/x/", "");
                    }
                    final ExternalId dependencyExternalId = externalIdFactory.createNameVersionExternalId(Forge.GOLANG, packageName, version);
                    final Dependency dependency = new Dependency(packageName, version, dependencyExternalId);
                    graph.addChildToRoot(dependency);
                }
            }
        }

        return graph;
    }
}
