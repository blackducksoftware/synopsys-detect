/*
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.Forge
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.bomtool.conda.CondaListParser
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.type.ExecutableType
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput

import groovy.transform.TypeChecked

@Component
@TypeChecked
class CondaBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(CondaBomTool.class)

    @Autowired
    CondaListParser condaListParser

    @Autowired
    ExternalIdFactory externalIdFactory

    private String condaExecutablePath

    @Override
    public BomToolType getBomToolType() {
        BomToolType.CONDA
    }

    @Override
    public boolean isBomToolApplicable() {
        def containsFiles = detectFileManager.containsAllFiles(sourcePath, 'environment.yml')
        if (containsFiles) {
            condaExecutablePath = findExecutablePath(ExecutableType.CONDA, true, detectConfiguration.getCondaPath())
            if (!condaExecutablePath) {
                logger.warn("Could not find the ${executableManager.getExecutableName(ExecutableType.CONDA)} executable")
            }
        }

        containsFiles && condaExecutablePath
    }

    @Override
    public List<DetectCodeLocation> extractDetectCodeLocations() {
        List<String> condaListOptions = ['list']
        if (detectConfiguration.getCondaEnvironmentName()) {
            condaListOptions.addAll([
                '-n',
                detectConfiguration.getCondaEnvironmentName()
            ])
        }
        condaListOptions.add('--json')
        Executable condaListExecutable = new Executable(sourceDirectory, condaExecutablePath, condaListOptions)
        ExecutableOutput condaListOutput = executableRunner.execute(condaListExecutable)
        String listJsonText = condaListOutput.standardOutput

        ExecutableOutput condaInfoOutput = executableRunner.runExe(condaExecutablePath, 'info', '--json')
        String infoJsonText = condaInfoOutput.standardOutput

        DependencyGraph dependencyGraph = condaListParser.parse(listJsonText, infoJsonText)
        ExternalId externalId = externalIdFactory.createPathExternalId(Forge.ANACONDA, detectConfiguration.sourcePath)
        def detectCodeLocation = new DetectCodeLocation(BomToolType.CONDA, detectConfiguration.sourcePath, externalId, dependencyGraph)

        [detectCodeLocation]
    }
}
