/*
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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
package com.blackducksoftware.integration.hub.detect.bomtool.go

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.detect.bomtool.GoDepBomTool
import com.blackducksoftware.integration.hub.detect.util.ProjectInfoGatherer
import com.moandjiezana.toml.Toml

class GopkgLockParser {
    private final ProjectInfoGatherer projectInfoGatherer

    public GopkgLockParser(ProjectInfoGatherer projectInfoGatherer) {
        this.projectInfoGatherer = projectInfoGatherer
    }

    public List<DependencyNode> parseDepLock(String depLockContents) {
        List<DependencyNode> nodes = new ArrayList<>()
        GopkgLock gopkgLock = new Toml().read(depLockContents).to(GopkgLock.class)

        for (Project project : gopkgLock.projects) {
            String name = project.name
            String version = ''
            if (project?.version?.trim()) {
                version = project.version
            } else {
                version = project.revision
            }
            for (String pack : project.packages) {
                String packageName = name
                if (!pack.equals('.')) {
                    packageName = "${packageName}/${pack}"
                }
                if (packageName.startsWith('golang.org/x/')) {
                    packageName = packageName.replaceAll('golang.org/x/', '')
                }
                final ExternalId dependencyExternalId = new NameVersionExternalId(GoDepBomTool.GOLANG, packageName, version)
                final DependencyNode dependency = new DependencyNode(packageName, version, dependencyExternalId)
                nodes.add(dependency)
            }
        }

        return nodes
    }
}
