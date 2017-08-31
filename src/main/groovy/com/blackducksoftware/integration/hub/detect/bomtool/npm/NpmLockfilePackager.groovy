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
package com.blackducksoftware.integration.hub.detect.bomtool.npm

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeBuilder
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeImpl
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.blackducksoftware.integration.hub.detect.nameversion.builder.LinkedNameVersionNodeBuilder
import com.google.gson.Gson

import groovy.transform.TypeChecked

@Component
@TypeChecked
class NpmLockfilePackager {
    @Autowired
    Gson gson

    @Autowired
    NameVersionNodeTransformer nameVersionNodeTransformer

    public DependencyNode parse(String lockFileText) {
        NpmProject npmProject = gson.fromJson(lockFileText, NpmProject.class)

        NameVersionNode root = new NameVersionNodeImpl([name: npmProject.name, version: npmProject.version])
        NameVersionNodeBuilder builder = new LinkedNameVersionNodeBuilder(root)

        npmProject.dependencies.each { name, npmDependency ->
            NameVersionNode dependency = new NameVersionNodeImpl([name: name, version: npmDependency.version])
            builder.addChildNodeToParent(dependency, root)

            npmDependency.requires?.each { childName, childVersion ->
                NameVersionNode child = new NameVersionNodeImpl([name: childName, version: childVersion])
                builder.addChildNodeToParent(child, dependency)
            }
        }

        nameVersionNodeTransformer.createDependencyNode(Forge.NPM, builder.build())
    }
}
