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
import com.blackducksoftware.integration.hub.detect.bomtool.GoBomTool
import com.blackducksoftware.integration.hub.detect.util.ProjectInfoGatherer
import com.google.gson.Gson

class GoDepParser {
    private final Gson gson

    private final ProjectInfoGatherer projectInfoGatherer

    public GoDepParser(Gson gson, ProjectInfoGatherer projectInfoGatherer){
        this.gson = gson;
        this.projectInfoGatherer = projectInfoGatherer
    }

    public DependencyNode parseGoDep(String goDepContents) {
        GodepsFile goDepsFile = gson.fromJson(goDepContents, GodepsFile.class)
        //FIXME get version
        String goDepContentVersion = projectInfoGatherer.getDefaultProjectVersionName()
        final ExternalId goDepContentExternalId = new NameVersionExternalId(GoBomTool.GOLANG, goDepsFile.importPath, goDepContentVersion)
        final DependencyNode goDepNode = new DependencyNode(goDepsFile.importPath, goDepContentVersion, goDepContentExternalId)
        def children = new ArrayList<DependencyNode>()
        goDepsFile.deps.each {
            def version = ''
            if (it.comment?.trim() && it.comment.contains('v')) {
                version = it.comment.trim()
                if(version.contains('-')){
                    //v1.6-27-23859436879234678  should be transformed to v1.6
                    version = version.substring(0, version.lastIndexOf('-'))
                    version = version.substring(0, version.lastIndexOf('-'))
                }
            } else{
                version = it.rev.trim()
            }
            final ExternalId dependencyExternalId = new NameVersionExternalId(GoBomTool.GOLANG, it.importPath, version)
            final DependencyNode dependency = new DependencyNode(it.importPath, version, dependencyExternalId)
            children.add(dependency)
        }
        goDepNode.children = children
        goDepNode
    }
}
