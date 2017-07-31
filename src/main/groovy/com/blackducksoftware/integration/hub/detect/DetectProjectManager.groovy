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
import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.SimpleBdioDocument
import com.blackducksoftware.integration.hub.detect.bomtool.BomTool
import com.blackducksoftware.integration.hub.detect.hub.HubSignatureScanner
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.model.DetectProject
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager
import com.blackducksoftware.integration.util.ExcludedIncludedFilter
import com.blackducksoftware.integration.util.IntegrationEscapeUtil
import com.google.gson.Gson

@Component
class DetectProjectManager {
    private final Logger logger = LoggerFactory.getLogger(DetectProjectManager.class)

    @Autowired
    DetectConfiguration detectConfiguration

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

    @Autowired
    IntegrationEscapeUtil integrationEscapeUtil

    @Autowired
    DetectFileManager detectFileManager

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
        //ensure that the project name is set, use some reasonable defaults
        detectProject.projectName = getProjectName(detectProject.projectName)
        detectProject.projectVersionName = getProjectVersionName(detectProject.projectVersionName, detectProject.projectVersionHash)

        if (!foundAnyBomTools) {
            logger.info("Could not find any tools to run - will register ${detectConfiguration.sourcePath} for signature scanning of ${detectProject.projectName}/${detectProject.projectVersionName}")
            hubSignatureScanner.registerPathToScan(detectConfiguration.sourceDirectory)
        }

        detectProject
    }

    public List<File> createBdioFiles(DetectProject detectProject) {
        List<File> bdioFiles = []

        File aggregateBdioFile = null
        SimpleBdioDocument aggregateBdioDocument = null
        if (detectConfiguration.aggregateBomName) {
            aggregateBdioDocument = createAggregateSimpleBdioDocument(detectProject)
            final String filename = "${integrationEscapeUtil.escapeForUri(detectConfiguration.aggregateBomName)}.jsonld"
            aggregateBdioFile = new File(detectConfiguration.getOutputDirectory(), filename)
            if (aggregateBdioFile.exists()) {
                aggregateBdioFile.delete()
            }
        }

        detectProject.detectCodeLocations.each {
            if (detectConfiguration.aggregateBomName) {
                aggregateBdioDocument.components.addAll(dependencyNodeTransformer.addComponentsGraph(aggregateBdioDocument.project, it.dependencies))
            } else {
                if (it.dependencies) {
                    final SimpleBdioDocument simpleBdioDocument = createSimpleBdioDocument(detectProject, it)
                    final String filename = it.createBdioFilename(integrationEscapeUtil, detectFileManager.extractFinalPieceFromPath(it.sourcePath), detectProject.projectName, detectProject.projectVersionName)
                    final File outputFile = new File(detectConfiguration.getOutputDirectory(), filename)
                    if (outputFile.exists()) {
                        outputFile.delete()
                    }
                    final File createdBdioFile = writeSimpleBdioDocument(outputFile, simpleBdioDocument)
                    bdioFiles.add(createdBdioFile)
                } else {
                    logger.debug("Could not find any dependencies for code location ${it.sourcePath}")
                }
            }
        }

        if (aggregateBdioFile != null && aggregateBdioDocument != null) {
            writeSimpleBdioDocument(aggregateBdioFile, aggregateBdioDocument)
        }

        bdioFiles
    }

    private SimpleBdioDocument createAggregateSimpleBdioDocument(DetectProject detectProject) {
        createSimpleBdioDocument(detectProject, '', detectProject.projectName, bdioPropertyHelper.createExternalIdentifier('', detectProject.projectName), [] as Set)
    }

    private SimpleBdioDocument createSimpleBdioDocument(DetectProject detectProject, DetectCodeLocation detectCodeLocation) {
        final String codeLocationName = detectProject.getCodeLocationName(detectCodeLocation.bomToolType, detectFileManager.extractFinalPieceFromPath(detectCodeLocation.sourcePath), 'Hub Detect Tool')
        final String projectId = detectCodeLocation.bomToolProjectExternalId.createDataId()
        final BdioExternalIdentifier projectExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(detectCodeLocation.bomToolProjectExternalId)

        createSimpleBdioDocument(detectProject, codeLocationName, projectId, projectExternalIdentifier, detectCodeLocation.dependencies)
    }

    private SimpleBdioDocument createSimpleBdioDocument(DetectProject detectProject, String codeLocationName, String projectId, BdioExternalIdentifier projectExternalIdentifier, Set<DependencyNode> dependencies) {
        final String projectName = detectProject.projectName
        final String projectVersionName = detectProject.projectVersionName

        final BdioBillOfMaterials bdioBillOfMaterials = bdioNodeFactory.createBillOfMaterials(codeLocationName, projectName, projectVersionName)
        final BdioProject project = bdioNodeFactory.createProject(projectName, projectVersionName, projectId, projectExternalIdentifier)

        final List<BdioComponent> bdioComponents = dependencyNodeTransformer.addComponentsGraph(project, dependencies)

        final SimpleBdioDocument simpleBdioDocument = new SimpleBdioDocument()
        simpleBdioDocument.billOfMaterials = bdioBillOfMaterials
        simpleBdioDocument.project = project
        simpleBdioDocument.components = bdioComponents

        simpleBdioDocument
    }

    private File writeSimpleBdioDocument(File outputFile, SimpleBdioDocument simpleBdioDocument) {
        final BdioWriter bdioWriter = new BdioWriter(gson, new FileOutputStream(outputFile))
        try {
            bdioWriter.writeSimpleBdioDocument(simpleBdioDocument)
        } finally {
            bdioWriter.close()
        }
        logger.info("BDIO Generated: " + outputFile.getAbsolutePath())

        outputFile
    }

    String getProjectName(final String defaultProjectName) {
        String projectName = defaultProjectName?.trim()

        if (detectConfiguration.getProjectName()) {
            projectName = detectConfiguration.getProjectName()
        } else if (!projectName && detectConfiguration.sourcePath) {
            String finalSourcePathPiece = detectFileManager.extractFinalPieceFromPath(detectConfiguration.sourcePath)
            projectName = finalSourcePathPiece
        }

        projectName
    }

    String getProjectVersionName(final String defaultVersionName, final String bomToolFileHash) {
        String projectVersion = defaultVersionName?.trim()

        if (detectConfiguration.getProjectVersionName()) {
            projectVersion = detectConfiguration.getProjectVersionName()
        } else if (!projectVersion) {
            projectVersion = 'Detect Unknown Version'
        }

        projectVersion
    }

}
