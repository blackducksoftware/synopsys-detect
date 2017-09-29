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

import org.apache.commons.codec.digest.DigestUtils
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.BdioNodeFactory
import com.blackducksoftware.integration.hub.bdio.BdioPropertyHelper
import com.blackducksoftware.integration.hub.bdio.BdioWriter
import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraphTransformer
import com.blackducksoftware.integration.hub.bdio.model.BdioBillOfMaterials
import com.blackducksoftware.integration.hub.bdio.model.BdioExternalIdentifier
import com.blackducksoftware.integration.hub.bdio.model.BdioNode
import com.blackducksoftware.integration.hub.bdio.model.BdioProject
import com.blackducksoftware.integration.hub.bdio.model.SimpleBdioDocument
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.detect.bomtool.BomTool
import com.blackducksoftware.integration.hub.detect.hub.HubSignatureScanner
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.model.DetectProject
import com.blackducksoftware.integration.hub.detect.summary.DetectSummary
import com.blackducksoftware.integration.hub.detect.summary.Result
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager
import com.blackducksoftware.integration.util.ExcludedIncludedFilter
import com.blackducksoftware.integration.util.IntegrationEscapeUtil
import com.google.gson.Gson

import groovy.transform.TypeChecked

@Component
@TypeChecked
class DetectProjectManager {
    private final Logger logger = LoggerFactory.getLogger(DetectProjectManager.class)

    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    BdioPropertyHelper bdioPropertyHelper

    @Autowired
    BdioNodeFactory bdioNodeFactory

    @Autowired
    DependencyGraphTransformer dependencyGraphTransformer

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

    @Autowired
    DetectSummary detectSummary

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
                    detectSummary.addApplicableBomToolType(bomTool.getBomToolType())
                    foundAnyBomTools = true
                    List<DetectCodeLocation> codeLocations = bomTool.extractDetectCodeLocations(detectProject)
                    if (codeLocations != null && codeLocations.size() > 0) {
                        detectSummary.setBomToolResult(bomTool.getBomToolType(), Result.SUCCESS)
                        detectProject.addAllDetectCodeLocations(codeLocations)
                    } else {
                        logger.error("Did not find any projects from ${bomToolTypeString} even though it applied.")
                        detectSummary.setBomToolResult(bomTool.getBomToolType(), Result.FAILURE)
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
        detectProject.setProjectNameIfNotSet(getProjectName(detectProject.projectName))
        detectProject.setProjectNameIfNotSet(getProjectVersionName(detectProject.projectVersionName))

        if (!foundAnyBomTools) {
            logger.info("No package managers were detected - will register ${detectConfiguration.sourcePath} for signature scanning of ${detectProject.projectName}/${detectProject.projectVersionName}")
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

        Map<ExternalId, BdioNode> nodeMap = new HashMap<ExternalId, BdioNode>();
        detectProject.detectCodeLocations.each {
            if (detectConfiguration.aggregateBomName) {
                def components = dependencyGraphTransformer.transformDependencyGraph(it.dependencyGraph, aggregateBdioDocument.project, it.dependencyGraph.getRootDependencies(), nodeMap)
                aggregateBdioDocument.components.addAll(components)
            } else {
                if (it.dependencyGraph) {
                    final SimpleBdioDocument simpleBdioDocument = createSimpleBdioDocument(detectProject, it)
                    String projectPath = detectFileManager.extractFinalPieceFromPath(it.sourcePath)
                    String projectName = detectProject.projectName
                    String projectVersionName = detectProject.projectVersionName
                    final String filename = createBdioFilename(it.bomToolType, projectPath, projectName, projectVersionName)
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

    private String createBdioFilename(BomToolType bomToolType, String finalSourcePathPiece, String projectName, String projectVersionName) {
        def names = [finalSourcePathPiece, projectName, projectVersionName]
        names.sort { -it.size() }
        String filename = generateFilename(bomToolType, finalSourcePathPiece, projectName, projectVersionName)
        for (int i = 0; (filename.length() >= 255) && (i < 3); i++) {
            names[i] = DigestUtils.sha1Hex(names[i])
            if (names[i].length() > 15) {
                names[i] = names[i].substring(0, 15)
            }

            filename = generateFilename(bomToolType, names[0], names[1], names[2])
        }

        filename
    }

    private String generateFilename(BomToolType bomToolType, String finalSourcePathPiece, String projectName, String projectVersionName) {
        List<String> safePieces = [bomToolType.toString(), projectName, projectVersionName, finalSourcePathPiece, 'bdio'].collect { integrationEscapeUtil.escapeForUri(it) }

        String filename = (safePieces as Iterable).join('_') + '.jsonld'
        filename
    }

    private SimpleBdioDocument createAggregateSimpleBdioDocument(DetectProject detectProject) {
        //we are hand making the aggregate because we don't have enough information to properly transform
        //we could change transformer to also transform a graph even though it doesn't have enough information
        //to rebuild the projects bdio nodes - but it seems like that is a waste of effort to save a few lines here.
        String hubCodeLocationName = '';
        String projectName = detectProject.getProjectName();
        BdioExternalIdentifier externalIdentifier = bdioPropertyHelper.createExternalIdentifier('', detectProject.projectName)
        final BdioBillOfMaterials billOfMaterials = bdioNodeFactory.createBillOfMaterials(hubCodeLocationName, projectName, '');

        final String projectId = projectName;
        final BdioProject project = bdioNodeFactory.createProject(projectName, '', projectId, externalIdentifier);

        final SimpleBdioDocument simpleBdioDocument = new SimpleBdioDocument();
        simpleBdioDocument.billOfMaterials = billOfMaterials;
        simpleBdioDocument.project = project;
        simpleBdioDocument
    }

    private SimpleBdioDocument createSimpleBdioDocument(DetectProject detectProject, DetectCodeLocation detectCodeLocation) {
        final String codeLocationName = detectProject.getBomToolCodeLocationName(detectCodeLocation.bomToolType, detectFileManager.extractFinalPieceFromPath(detectCodeLocation.sourcePath), detectConfiguration.getProjectCodeLocationPrefix())
        final String projectId = detectCodeLocation.bomToolProjectExternalId.createBdioId()

        createSimpleBdioDocument(detectProject, codeLocationName, projectId, detectCodeLocation.bomToolProjectExternalId, detectCodeLocation.dependencyGraph)
    }

    private SimpleBdioDocument createSimpleBdioDocument(DetectProject detectProject, String codeLocationName, String projectId, ExternalId projectExternalId, DependencyGraph dependencies) {

        final String projectName = detectProject.projectName
        final String projectVersionName = detectProject.projectVersionName
        final SimpleBdioDocument simpleBdioDocument = dependencyGraphTransformer.transformDependencyGraph(codeLocationName, projectName, projectVersionName, projectExternalId, dependencies)

        String hubDetectVersion = detectConfiguration.getBuildInfo().getDetectVersion()
        def detectVersionData = ['detectVersion' : hubDetectVersion]

        simpleBdioDocument.billOfMaterials.customData = detectVersionData
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

    String getProjectVersionName(final String defaultVersionName) {
        String projectVersion = defaultVersionName?.trim()

        if (detectConfiguration.projectVersionName) {
            projectVersion = detectConfiguration.projectVersionName
        } else if (!projectVersion) {
            if ('timestamp' == detectConfiguration.defaultProjectVersionScheme) {
                String timeformat = detectConfiguration.defaultProjectVersionTimeformat
                String timeString = DateTimeFormat.forPattern(timeformat).withZoneUTC().print(DateTime.now().withZone(DateTimeZone.UTC))
                projectVersion = timeString
            } else {
                projectVersion = detectConfiguration.defaultProjectVersionText
            }
        }

        projectVersion
    }

}
