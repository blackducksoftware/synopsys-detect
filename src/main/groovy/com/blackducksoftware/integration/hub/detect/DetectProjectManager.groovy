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
package com.blackducksoftware.integration.hub.detect

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.BdioNodeFactory
import com.blackducksoftware.integration.hub.bdio.simple.BdioPropertyHelper
import com.blackducksoftware.integration.hub.bdio.simple.BdioWriter
import com.blackducksoftware.integration.hub.bdio.simple.DependencyNodeTransformer
import com.blackducksoftware.integration.hub.bdio.simple.model.BdioBillOfMaterials
import com.blackducksoftware.integration.hub.bdio.simple.model.BdioComponent
import com.blackducksoftware.integration.hub.bdio.simple.model.BdioExternalIdentifier
import com.blackducksoftware.integration.hub.bdio.simple.model.BdioProject
import com.blackducksoftware.integration.hub.bdio.simple.model.SimpleBdioDocument
import com.blackducksoftware.integration.hub.detect.bomtool.BomTool
import com.blackducksoftware.integration.hub.detect.bomtool.output.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.bomtool.output.DetectProject
import com.blackducksoftware.integration.hub.detect.hub.HubSignatureScanner
import com.blackducksoftware.integration.hub.detect.type.BomToolType
import com.blackducksoftware.integration.hub.detect.util.ProjectInfoGatherer
import com.blackducksoftware.integration.util.ExcludedIncludedFilter
import com.blackducksoftware.integration.util.IntegrationEscapeUtil
import com.google.gson.Gson

@Component
class DetectProjectManager {
    private final Logger logger = LoggerFactory.getLogger(DetectProjectManager.class)

    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    ProjectInfoGatherer projectInfoGatherer

    @Autowired
    BdioPropertyHelper bdioPropertyHelper

    @Autowired
    BdioNodeFactory bdioNodeFactory

    @Autowired
    DependencyNodeTransformer dependencyNodeTransformer

    @Autowired
    Gson gson

    @Autowired
    List<BomTool> bomTools

    @Autowired
    HubSignatureScanner hubSignatureScanner

    private boolean foundAnyBomTools

    public DetectProject createDetectProject() {
        DetectProject detectProject = new DetectProject()

        String excludedBomTools = detectConfiguration.excludedBomToolTypes
        String includedBomTools = detectConfiguration.includedBomToolTypes
        final ExcludedIncludedFilter toolFilter = new ExcludedIncludedFilter(excludedBomTools, includedBomTools)

        for (BomTool bomTool : bomTools) {
            final BomToolType bomToolType = bomTool.bomToolType
            final String bomToolTypeString = bomToolType.toString()
            try {
                if (!toolFilter.shouldInclude(bomToolTypeString)) {
                    logger.debug("Skipping ${bomToolTypeString}.")
                    continue
                }

                if (bomTool.isBomToolApplicable() && detectConfiguration.shouldRun(bomTool)) {
                    logger.info("${bomToolTypeString} applies given the current configuration.")
                    foundAnyBomTools = true
                    List<DetectCodeLocation> codeLocations = bomTool.extractDetectCodeLocations()
                    if (codeLocations != null && codeLocations.size() > 0) {
                        detectProject.addAllDetectCodeLocations(codeLocations)
                    } else {
                        //currently, Docker creates and uploads the bdio files itself, so there's nothing for Detect to do
                        if (BomToolType.DOCKER != bomToolType) {
                            logger.error("Did not find any projects from ${bomToolTypeString} even though it applied.")
                        }
                    }
                }
            } catch (final Exception e) {
                // any bom tool failure should not prevent other bom tools from running
                logger.error("${bomToolTypeString} threw an Exception: ${e.message}")
                if (logger.isTraceEnabled()) {
                    e.printStackTrace()
                }
            }
        }

        //if none of the bom tools could determine a project/version, use some reasonable defaults
        detectProject.projectName = projectInfoGatherer.getProjectName(detectConfiguration.sourcePath, detectProject.projectName)
        detectProject.projectVersionName = projectInfoGatherer.getProjectVersionName(detectProject.projectVersionName, detectProject.projectVersionHash)

        if (!foundAnyBomTools) {
            logger.info("Could not find any tools to run - will register ${detectConfiguration.sourcePath} for signature scanning of ${detectProject.projectName}/${detectProject.projectVersionName}")
            hubSignatureScanner.registerDirectoryToScan(detectConfiguration.sourceDirectory)
        }

        detectProject
    }

    public List<File> createBdioFiles(DetectProject detectProject) {
        List<File> bdioFiles = []
        detectProject.detectCodeLocations.each {
            File createdBdioFile = createBdioFile(detectProject, it)
            bdioFiles.add(createdBdioFile)
        }

        bdioFiles
    }

    private File createBdioFile(DetectProject detectProject, DetectCodeLocation detectCodeLocation) {
        String projectName = detectProject.projectName
        String projectVersionName = detectProject.projectVersionName
        String codeLocationName = projectInfoGatherer.getCodeLocationName(detectCodeLocation.bomToolType, detectCodeLocation.sourcePath, projectName, projectVersionName)

        final IntegrationEscapeUtil escapeUtil = new IntegrationEscapeUtil()
        final String safeProjectName = escapeUtil.escapeForUri(projectName)
        final String safeVersionName = escapeUtil.escapeForUri(projectVersionName)
        final String filename = String.format("%s_%s_%s_bdio.jsonld", detectCodeLocation.bomToolType.toString(), safeProjectName, safeVersionName)
        final File outputFile = new File(detectConfiguration.getOutputDirectory(), filename)
        if (outputFile.exists()) {
            outputFile.delete()
        }

        BdioBillOfMaterials bdioBillOfMaterials = bdioNodeFactory.createBillOfMaterials(codeLocationName, projectName, projectVersionName)

        final String projectId = detectCodeLocation.bomToolProjectExternalId.createDataId()
        final BdioExternalIdentifier projectExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(detectCodeLocation.bomToolProjectExternalId)
        final BdioProject project = bdioNodeFactory.createProject(projectName, projectVersionName, projectId, projectExternalIdentifier)

        final List<BdioComponent> bdioComponents = dependencyNodeTransformer.addComponentsGraph(project, detectCodeLocation.dependencies)

        SimpleBdioDocument simpleBdioDocument = new SimpleBdioDocument()
        simpleBdioDocument.billOfMaterials = bdioBillOfMaterials
        simpleBdioDocument.project = project
        simpleBdioDocument.components = bdioComponents

        final BdioWriter bdioWriter = new BdioWriter(gson, new FileOutputStream(outputFile))
        try {
            bdioWriter.writeSimpleBdioDocument(simpleBdioDocument)
        } finally {
            bdioWriter.close()
        }
        logger.info("BDIO Generated: " + outputFile.getAbsolutePath())

        outputFile
    }
}
