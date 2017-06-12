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
package com.blackducksoftware.integration.hub.detect.bomtool.nuget

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
