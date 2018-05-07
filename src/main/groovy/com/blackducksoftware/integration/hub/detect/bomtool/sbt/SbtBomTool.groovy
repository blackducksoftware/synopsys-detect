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
package com.blackducksoftware.integration.hub.detect.bomtool.sbt

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolExtractionResult
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.models.SbtProject
import com.blackducksoftware.integration.hub.detect.hub.HubSignatureScanner
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation

import groovy.transform.TypeChecked

@Component
@TypeChecked
class SbtBomTool extends BomTool<SbtApplicableResult> {
    private final Logger logger = LoggerFactory.getLogger(SbtBomTool.class)

    static final String BUILD_SBT_FILENAME = 'build.sbt'
    static final String REPORT_FILE_DIRECTORY = "${File.separator}target${File.separator}resolution-cache${File.separator}reports"
    static final String REPORT_SEARCH_PATTERN = 'resolution-cache'
    static final String REPORT_DIRECTORY = "reports"
    static final String REPORT_FILE_PATTERN = '*.xml'
    static final String PROJECT_FOLDER = 'project'

    @Autowired
    HubSignatureScanner hubSignatureScanner

    BomToolType getBomToolType() {
        return BomToolType.SBT
    }

    SbtApplicableResult isBomToolApplicable(File directory) {
        File buildDotSbt = detectFileFinder.findFile(directory, BUILD_SBT_FILENAME)

        if (buildDotSbt) {
            return new SbtApplicableResult(directory, buildDotSbt);
        } else {
            return null
        }
    }

    BomToolExtractionResult extractDetectCodeLocations(SbtApplicableResult applicable) {
        String included = detectConfiguration.getSbtIncludedConfigurationNames()
        String excluded = detectConfiguration.getSbtExcludedConfigurationNames()

        int depth = detectConfiguration.getSearchDepth()

        SbtPackager packager = new SbtPackager(externalIdFactory, detectFileFinder);
        SbtProject project = packager.extractProject(applicable.directoryString, depth, included, excluded)

        List<DetectCodeLocation> codeLocations = new ArrayList<DetectCodeLocation>()

        project.modules.each { module ->
            DetectCodeLocation codeLocation = new DetectCodeLocation.Builder(getBomToolType(), module.name, project.projectExternalId, module.graph).bomToolProjectName(project.projectName).bomToolProjectVersionName(project.projectVersion).build()
            codeLocations.add(codeLocation)
        }

        if (!codeLocations) {
            logger.error("Unable to find any dependency information.")
        }

        bomToolExtractionResultsFactory.fromCodeLocations(codeLocations, getBomToolType(), applicable.directory)
    }


}
