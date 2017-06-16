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
import com.blackducksoftware.integration.hub.detect.bomtool.gradle.GradleInitScriptPackager
import com.blackducksoftware.integration.hub.detect.type.BomToolType
import com.blackducksoftware.integration.hub.detect.type.ExecutableType

@Component
class GradleBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(GradleBomTool.class)

    static final String BUILD_GRADLE = 'build.gradle'

    @Autowired
    GradleInitScriptPackager gradleInitScriptPackager

    Map<String, String> matchingSourcePathToGradleExecutable = [:]

    BomToolType getBomToolType() {
        return BomToolType.GRADLE
    }

    boolean isBomToolApplicable() {
        detectConfiguration.getSourcePaths().each { sourcePath ->
            def gradleExecutable = findGradleExecutable(sourcePath)
            def buildGradle = detectFileManager.findFile(sourcePath, BUILD_GRADLE)
            if (gradleExecutable && buildGradle) {
                matchingSourcePathToGradleExecutable.put(sourcePath, gradleExecutable)
            }
        }

        !matchingSourcePathToGradleExecutable.isEmpty()
    }

    List<DependencyNode> extractDependencyNodes() {
        List<DependencyNode> projectNodes = []
        matchingSourcePathToGradleExecutable.each { sourcePath, gradleExecutable ->
            DependencyNode rootProjectNode = gradleInitScriptPackager.extractRootProjectNode(sourcePath, gradleExecutable)
            projectNodes.add(rootProjectNode)
        }

        projectNodes
    }

    private String findGradleExecutable(String sourcePath) {
        String gradlePath = detectConfiguration.getGradlePath()
        if (StringUtils.isBlank(gradlePath)) {
            logger.debug('detect.gradle.path not set in config - first try to find the gradle wrapper')
            gradlePath = executableManager.getPathOfExecutable(sourcePath, ExecutableType.GRADLEW)
        }

        if (StringUtils.isBlank(gradlePath)) {
            logger.debug('gradle wrapper not found - trying to find gradle on the PATH')
            gradlePath = executableManager.getPathOfExecutable(ExecutableType.GRADLE)
        }

        gradlePath
    }
}