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
package com.blackducksoftware.integration.hub.detect.bomtool.cpan

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.Forge
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.detect.bomtool.BomTool
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolExtractionResult
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.type.ExecutableType
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput

import groovy.transform.TypeChecked

@Component
@TypeChecked
class CpanBomTool extends BomTool<CpanApplicableResult> {
    private final Logger logger = LoggerFactory.getLogger(CpanBomTool.class)

    @Autowired
    CpanPackager cpanPackager

    @Override
    public BomToolType getBomToolType() {
        BomToolType.CPAN
    }

    @Override
    public CpanApplicableResult isBomToolApplicable(File directory) {
        def makefile = detectFileFinder.findFile(directory, 'Makefile.PL')
        if (makefile != null && makefile.exists()) {
            def cpanExecutablePath = executableManager.getExecutablePathOrOverride(ExecutableType.CPAN, true, directory, detectConfiguration.getCpanPath())
            def cpanmExecutablePath = executableManager.getExecutablePathOrOverride(ExecutableType.CPANM, true, directory, detectConfiguration.getCpanmPath())
            if (cpanExecutablePath && cpanmExecutablePath) {
                return new CpanApplicableResult(directory, makefile, cpanExecutablePath, cpanmExecutablePath);
            } else if (!cpanExecutablePath) {
                logger.warn("Could not find the ${executableManager.getExecutableName(ExecutableType.CPAN)} executable")
            } else if (!cpanmExecutablePath) {
                logger.warn("Could not find the ${executableManager.getExecutableName(ExecutableType.CPANM)} executable")
            }
        }

        return null;
    }

    @Override
    public BomToolExtractionResult extractDetectCodeLocations(CpanApplicableResult applicable) {
        ExecutableOutput cpanListOutput = executableRunner.runExe(applicable.cpanExePath, '-l')
        List<String> listText = cpanListOutput.standardOutputAsList

        ExecutableOutput showdepsOutput = executableRunner.runExe(applicable.cpanmExePath, '--showdeps', '.')
        List<String> showdeps = showdepsOutput.standardOutputAsList

        DependencyGraph dependencyGraph = cpanPackager.makeDependencyGraph(listText, showdeps)
        ExternalId externalId = externalIdFactory.createPathExternalId(Forge.CPAN, detectConfiguration.sourcePath)
        def detectCodeLocation = new DetectCodeLocation.Builder(BomToolType.CPAN, detectConfiguration.sourcePath, externalId, dependencyGraph).build()

        bomToolExtractionResultsFactory.fromCodeLocations([detectCodeLocation], getBomToolType(), applicable.directory)
    }
}
