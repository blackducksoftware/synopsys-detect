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

import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.bomtool.maven.MavenPackager
import com.blackducksoftware.integration.hub.detect.type.BomToolType
import com.blackducksoftware.integration.hub.detect.type.ExecutableType

@Component
class MavenBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(MavenBomTool.class)

    static final String POM_FILENAME = 'pom.xml'
    static final String POM_WRAPPER_FILENAME = 'pom.groovy'

    @Autowired
    MavenPackager mavenPackager

    List<String> matchingSourcePaths = []

    BomToolType getBomToolType() {
        return BomToolType.MAVEN
    }

    boolean isBomToolApplicable() {
        matchingSourcePaths += sourcePathSearcher.findSourcePathsContainingFilenamePattern(POM_FILENAME)
        matchingSourcePaths += sourcePathSearcher.findSourcePathsContainingFilenamePattern(POM_WRAPPER_FILENAME)
        def mvnExecutable = false
        for(String sourcePath : matchingSourcePaths) {
            if(findMavenExecutablePath(sourcePath)){
                mvnExecutable = true
                break
            }
        }
        mvnExecutable && !matchingSourcePaths.isEmpty()
    }

    List<DependencyNode> extractDependencyNodes() {
        List<DependencyNode> projectNodes = []
        matchingSourcePaths.each {
            projectNodes.addAll(mavenPackager.makeDependencyNodes(it, findMavenExecutablePath(it)))
        }

        projectNodes
    }

    private String findMavenExecutablePath(String sourcePath) {
        if (StringUtils.isNotBlank(detectConfiguration.getMavenPath())) {
            return detectConfiguration.getMavenPath()
        }

        String wrapperPath = executableManager.getPathOfExecutable(sourcePath, ExecutableType.MVNW)
        if(wrapperPath) {
            return wrapperPath
        }

        executableManager.getPathOfExecutable(ExecutableType.MVN)
    }
}