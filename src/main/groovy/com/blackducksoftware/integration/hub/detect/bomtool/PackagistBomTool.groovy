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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.bomtool.output.DetectProject
import com.blackducksoftware.integration.hub.detect.bomtool.packagist.PackagistParser
import com.blackducksoftware.integration.hub.detect.type.BomToolType

@Component
class PackagistBomTool extends BomTool {
    static final COMPOSER_LOCK = 'composer.lock'
    static final COMPOSER_JSON = 'composer.json'

    private final Logger logger = LoggerFactory.getLogger(PackagistBomTool.class)

    def composerLockAndJsonPaths = []

    @Autowired
    PackagistParser packagistParser

    @Override
    public BomToolType getBomToolType() {
        BomToolType.PACKAGIST
    }

    @Override
    public boolean isBomToolApplicable() {
        def composerLockPaths = sourcePathSearcher.findFilenamePattern(COMPOSER_LOCK)
        def composerJsonPaths = sourcePathSearcher.findFilenamePattern(COMPOSER_JSON)

        composerLockPaths.each { path ->
            if(composerJsonPaths.contains(path)) {
                composerLockAndJsonPaths.add(path)
                composerJsonPaths.remove(path)
            } else {
                logger.warn("${COMPOSER_LOCK} was located in ${path}, but no ${COMPOSER_JSON}. Please add a ${COMPOSER_JSON} file and try again.")
            }
        }

        composerJsonPaths.each { path ->
            logger.warn("${COMPOSER_JSON} was located in ${path}, but no ${COMPOSER_LOCK}. Please install dependencies and try again.")
        }

        composerLockAndJsonPaths
    }

    @Override
    public List<DetectProject> extractDetectProjects() {
        List<DetectProject> projects = []

        composerLockAndJsonPaths.each { path ->
            DetectProject detectProject = new DetectProject(new File(path))
            detectProject.dependencyNodes = [
                packagistParser.getDependencyNodeFromProject(path)
            ]
            projects.add(detectProject)
        }

        projects
    }
}
