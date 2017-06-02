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
package com.blackducksoftware.integration.hub.packman.packagemanager.go

import org.springframework.beans.factory.annotation.Autowired

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.packman.util.ProjectInfoGatherer
import com.google.gson.Gson

class GoDepParser {
    @Autowired
    Gson gson

    private final ProjectInfoGatherer projectInfoGatherer

    public GoDepParser(ProjectInfoGatherer projectInfoGatherer) {
        this.projectInfoGatherer = projectInfoGatherer
    }

    public DependencyNode parseGoDep(final String goDepContents) {
        GodepsFile goDepsFile = gson.fromJson(goDepContents, GodepsFile.class)
        //FIXME get version
        String goDepContentVersion = projectInfoGatherer.getDefaultProjectVersionName()
        final ExternalId goDepContentExternalId = new NameVersionExternalId(Forge.GOGET, goDepsFile.importPath, goDepContentVersion)
        final DependencyNode goDepContent = new DependencyNode(goDepsFile.importPath, goDepContentVersion, goDepContentExternalId)
        def children = new ArrayList<DependencyNode>()
        goDepsFile.deps.each {
            def version = ''
            if (it.comment?.trim()) {
                version = it.comment.trim()
            } else{
                version = it.rev.trim()
            }
            final ExternalId dependencyExternalId = new NameVersionExternalId(Forge.GOGET, it.importPath, version)
            final DependencyNode dependency = new DependencyNode(it.importPath, version, dependencyExternalId)
            children.add(dependency)
        }
        goDepContent.children = children
        goDepContent
    }
}
