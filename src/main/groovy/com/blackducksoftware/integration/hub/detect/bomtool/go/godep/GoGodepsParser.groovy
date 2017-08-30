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
package com.blackducksoftware.integration.hub.detect.bomtool.go.godep

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.detect.bomtool.GoDepBomTool
import com.google.gson.Gson

import groovy.transform.TypeChecked

@TypeChecked
class GoGodepsParser {
    private final Gson gson

    public GoGodepsParser(Gson gson) {
        this.gson = gson
    }

    public List<DependencyNode> extractProjectDependencies(String goDepContents) {
        GodepsFile goDepsFile = gson.fromJson(goDepContents, GodepsFile.class)
        List<DependencyNode> children = []
        goDepsFile.deps.each { GodepDependency dep ->
            def version = ''
            if (dep.comment?.trim()) {
                version = dep.comment.trim()
                //TODO test with kubernetes
                if (version.matches('.*-.*-.*')) {
                    //v1.6-27-23859436879234678  should be changed to v1.6
                    version = version.substring(0, version.lastIndexOf('-'))
                    version = version.substring(0, version.lastIndexOf('-'))
                }
            } else {
                version = dep.rev.trim()
            }
            final ExternalId dependencyExternalId = new NameVersionExternalId(GoDepBomTool.GOLANG, dep.importPath, version)
            final DependencyNode dependency = new DependencyNode(dep.importPath, version, dependencyExternalId)
            children.add(dependency)
        }
        children
    }
}
