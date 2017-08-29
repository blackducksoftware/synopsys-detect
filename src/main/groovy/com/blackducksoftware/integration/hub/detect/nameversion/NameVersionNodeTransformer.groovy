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
package com.blackducksoftware.integration.hub.detect.nameversion

import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId

@Component
class NameVersionNodeTransformer {
    public DependencyNode createDependencyNode(Forge defaultForge, NameVersionNodeBuilder nameVersionNodeBuilder) {
        createDependencyNode(nameVersionNodeBuilder.build())
    }

    public DependencyNode createDependencyNode(Forge defaultForge, NameVersionNode nameVersionNode) {
        final Forge forge = nameVersionNode.metadata?.forge ? nameVersionNode.metadata.forge : defaultForge
        def externalId = new NameVersionExternalId(forge, nameVersionNode.name, nameVersionNode.version)
        def dependencyNode = new DependencyNode(nameVersionNode.name, nameVersionNode.version, externalId)
        nameVersionNode.children.each {
            dependencyNode.children.add(createDependencyNode(defaultForge, it))
        }

        dependencyNode
    }
}
