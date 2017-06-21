/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
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

    public GopkgLockParser(ProjectInfoGatherer projectInfoGatherer){
        this.projectInfoGatherer = projectInfoGatherer
    }

    public List<DependencyNode> parseDepLock(String depLockContents) {
        List<DependencyNode> nodes = new ArrayList<>()
        GopkgLock gopkgLock = new Toml().read(depLockContents).to(GopkgLock.class)

        gopkgLock.projects.each { project ->
            String name = project.name
            String version = ''
            if (project?.version?.trim()) {
                version = project.version
            } else {
                version = project.revision
            }
            project.packages.each{
                if (!it.equals('.')){
                    name = "${name}/${it}"
                }
                final ExternalId dependencyExternalId = new NameVersionExternalId(GoDepBomTool.GOLANG, name, version)
                final DependencyNode dependency = new DependencyNode(name, version, dependencyExternalId)
                nodes.add(dependency)
            }
        }

        return nodes
    }
}
