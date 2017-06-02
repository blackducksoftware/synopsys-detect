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
package com.blackducksoftware.integration.hub.packman.bomtool.nuget

import java.nio.charset.StandardCharsets

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.google.gson.Gson

@Component
class NugetNodeTransformer {

    @Autowired
    Gson gson

    DependencyNode parse(File dependencyNodeFile) {
        final String dependencyNodeJson = dependencyNodeFile.getText(StandardCharsets.UTF_8.name())
        final NugetNode solution = gson.fromJson(dependencyNodeJson, NugetNode.class)
        nugetNodeTransformer(solution)
    }

    private DependencyNode nugetNodeTransformer(final NugetNode node) {
        final String name = node.artifact
        final String version = node.version
        final ExternalId externalId = new NameVersionExternalId(Forge.NUGET, name, version)
        final DependencyNode dependencyNode = new DependencyNode(name, version, externalId)
        node.children.each {
            dependencyNode.children.add(nugetNodeTransformer(it))
        }
        dependencyNode
    }
}
