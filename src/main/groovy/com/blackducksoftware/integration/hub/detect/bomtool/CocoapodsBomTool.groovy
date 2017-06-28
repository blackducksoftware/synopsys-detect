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
package com.blackducksoftware.integration.hub.detect.bomtool

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.bomtool.cocoapods.CocoapodsPackager
import com.blackducksoftware.integration.hub.detect.bomtool.output.DetectProject
import com.blackducksoftware.integration.hub.detect.type.BomToolType

@Component
class CocoapodsBomTool extends BomTool {
    @Autowired
    CocoapodsPackager cocoapodsPackager

    private List<String> matchingSourcePaths = []

    BomToolType getBomToolType() {
        return BomToolType.COCOAPODS
    }

    boolean isBomToolApplicable() {
        matchingSourcePaths = sourcePathSearcher.findFilenamePattern('Podfile.lock')

        !matchingSourcePaths.isEmpty()
    }

    List<DetectProject> extractDetectProjects() {
        List<DetectProject> projects = []
        matchingSourcePaths.each {
            File sourcePathFile = new File(it)
            DetectProject project = new DetectProject()
            project.targetName = sourcePathFile.getName()
            project.dependencyNodes = cocoapodsPackager.makeDependencyNodes(it)
            projects.add(project)
        }

        projects
    }
}