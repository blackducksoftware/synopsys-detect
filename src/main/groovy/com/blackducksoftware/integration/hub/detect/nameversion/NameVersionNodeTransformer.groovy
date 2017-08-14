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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId

@Component
class NameVersionNodeTransformer {
    private final Logger logger = LoggerFactory.getLogger(NameVersionNodeTransformer.class)

    public DependencyNode createDependencyNode(Forge forge, NameVersionNode nameVersionNode) {
        Stack<String> cyclicalStack = new Stack<>()

        createDependencyNodeHelper(forge, cyclicalStack, nameVersionNode)
    }

    private DependencyNode createDependencyNodeHelper(Forge forge, Stack<String> cyclicalStack, NameVersionNode nameVersionNode) {
        final NameVersionNode link = nameVersionNode.getLink()
        final String name = nameVersionNode.link ? link.name : nameVersionNode.name
        final String version = nameVersionNode.link ? link.version : nameVersionNode.version
        final List<NameVersionNode> children = nameVersionNode.link ? link.children : nameVersionNode.children
        final def externalId = new NameVersionExternalId(forge, name, version)
        final def dependencyNode = new DependencyNode(name, version, externalId)

        if(cyclicalStack.contains(name)) {
            return null
        }
        cyclicalStack.push(name)
        children.each {
            DependencyNode child = createDependencyNodeHelper(forge, cyclicalStack, it)
            if(child) {
                dependencyNode.children.add(child)
            } else {
                logger.info("Cyclical depdency [${it.name}] detected")
            }
        }
        cyclicalStack.pop()

        dependencyNode
    }
}
