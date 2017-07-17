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

import org.apache.commons.io.FilenameUtils
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
    IntegrationEscapeUtil escapeUtil

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
        detectProject.projectName = getProjectName(detectProject.projectName)
        detectProject.projectVersionName = getProjectVersionName(detectProject.projectVersionName, detectProject.projectVersionHash)

        if (!foundAnyBomTools) {
            logger.info("Could not find any tools to run - will register ${detectConfiguration.sourcePath} for signature scanning of ${detectProject.projectName}/${detectProject.projectVersionName}")
            hubSignatureScanner.registerDirectoryToScan(detectConfiguration.sourceDirectory)
        }

        detectProject
    }

    public List<File> createBdioFiles(DetectProject detectProject) {
        List<File> bdioFiles = []
        final String safeProjectName = escapeUtil.escapeForUri(detectProject.projectName)
        final String safeVersionName = escapeUtil.escapeForUri(detectProject.projectVersionName)

        if(detectConfiguration.aggregateBomName) {
            final SimpleBdioDocument aggregateBdioDocument = createAggregateSimpleBdioDocument(detectProject)
            detectProject.detectCodeLocations.each {
                aggregateBdioDocument.components += dependencyNodeTransformer.addComponentsGraph(aggregateBdioDocument.project, it.dependencies)
            }
            final String filename = "${escapeUtil.escapeForUri(detectConfiguration.aggregateBomName)}.jsonld"
            final File outputFile = new File(detectConfiguration.getOutputDirectory(), filename)
            if (outputFile.exists()) {
                outputFile.delete()
            }
            final File createdBdioFile = writeSimpleBdioDocument(outputFile, aggregateBdioDocument)
            bdioFiles += createdBdioFile
        } else {
            detectProject.detectCodeLocations.each {
                final SimpleBdioDocument simpleBdioDocument = createSimpleBdioDocument(detectProject, it)
                final String filename = "${it.bomToolType.toString()}_${safeProjectName}_${safeVersionName}_bdio.jsonld"
                final File outputFile = new File(detectConfiguration.getOutputDirectory(), filename)
                if (outputFile.exists()) {
                    outputFile.delete()
                }
                final File createdBdioFile = writeSimpleBdioDocument(outputFile, simpleBdioDocument)
                bdioFiles += createdBdioFile
            }
        }

        bdioFiles
    }

    private SimpleBdioDocument createAggregateSimpleBdioDocument(DetectProject detectProject) {
        String projectName = detectProject.projectName
        String projectVersionName = detectProject.projectVersionName

        final BdioBillOfMaterials bdioBillOfMaterials = bdioNodeFactory.createBillOfMaterials('', projectName, projectVersionName)
        final BdioExternalIdentifier projectExternalIdentifier = bdioPropertyHelper.createExternalIdentifier('', projectName)
        final BdioProject project = bdioNodeFactory.createProject(projectName, projectVersionName, projectName, projectExternalIdentifier)

        final SimpleBdioDocument simpleBdioDocument = new SimpleBdioDocument()
        simpleBdioDocument.billOfMaterials = bdioBillOfMaterials
        simpleBdioDocument.project = project

        simpleBdioDocument
    }

    private SimpleBdioDocument createSimpleBdioDocument(DetectProject detectProject, DetectCodeLocation detectCodeLocation) {
        String projectName = detectProject.projectName
        String projectVersionName = detectProject.projectVersionName
        String codeLocationName = getCodeLocationName(detectCodeLocation.bomToolType, detectCodeLocation.sourcePath, projectName, projectVersionName)

        final BdioBillOfMaterials bdioBillOfMaterials = bdioNodeFactory.createBillOfMaterials(codeLocationName, projectName, projectVersionName)

        final String projectId = detectCodeLocation.bomToolProjectExternalId.createDataId()
        final BdioExternalIdentifier projectExternalIdentifier = bdioPropertyHelper.createExternalIdentifier(detectCodeLocation.bomToolProjectExternalId)
        final BdioProject project = bdioNodeFactory.createProject(projectName, projectVersionName, projectId, projectExternalIdentifier)

        final List<BdioComponent> bdioComponents = dependencyNodeTransformer.addComponentsGraph(project, detectCodeLocation.dependencies)

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
            String finalSourcePathPiece = extractFinalPieceFromSourcePath(detectConfiguration.sourcePath)
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

    String getCodeLocationName(final BomToolType bomToolType, final String sourcePath, final String projectName, final String projectVersion) {
        String codeLocation = detectConfiguration.getProjectCodeLocationName()
        if (!codeLocation?.trim()) {
            String finalSourcePathPiece = extractFinalPieceFromSourcePath(sourcePath)
            codeLocation = String.format('%s/%s/%s', finalSourcePathPiece, projectName, projectVersion)
        }
        return String.format('%s/%s Hub Detect Export', bomToolType.toString(), codeLocation)
    }

    private String extractFinalPieceFromSourcePath(String sourcePath) {
        if (sourcePath == null || sourcePath.length() == 0) {
            return ''
        }
        String normalizedSourcePath = FilenameUtils.normalizeNoEndSeparator(sourcePath, true)
        normalizedSourcePath[normalizedSourcePath.lastIndexOf('/') + 1..-1]
    }
}
