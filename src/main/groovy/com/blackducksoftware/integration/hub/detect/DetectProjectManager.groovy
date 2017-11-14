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

import com.blackducksoftware.integration.hub.bdio.SimpleBdioFactory
import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.BdioNode
import com.blackducksoftware.integration.hub.bdio.model.Forge
import com.blackducksoftware.integration.hub.bdio.model.SimpleBdioDocument
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.detect.bomtool.BomTool
import com.blackducksoftware.integration.hub.detect.codelocation.CodeLocationName
import com.blackducksoftware.integration.hub.detect.codelocation.CodeLocationNameService
import com.blackducksoftware.integration.hub.detect.hub.HubSignatureScanner
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.model.DetectProject
import com.blackducksoftware.integration.hub.detect.summary.BomToolSummaryResult
import com.blackducksoftware.integration.hub.detect.summary.Result
import com.blackducksoftware.integration.hub.detect.summary.SummaryResultReporter
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager
import com.blackducksoftware.integration.util.ExcludedIncludedFilter
import com.blackducksoftware.integration.util.IntegrationEscapeUtil
import com.google.gson.Gson

import groovy.transform.TypeChecked

@Component
@TypeChecked
class DetectProjectManager implements SummaryResultReporter {
    private final Logger logger = LoggerFactory.getLogger(DetectProjectManager.class)

    @Autowired
    DetectInfo detectInfo

    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    SimpleBdioFactory simpleBdioFactory

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
    CodeLocationNameService codeLocationNameService

    private boolean foundAnyBomTools
    private Map<BomToolType, Result> bomToolSummaryResults = new HashMap<>();

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
                    bomToolSummaryResults.put(bomTool.getBomToolType(), Result.FAILURE);
                    foundAnyBomTools = true
                    List<DetectCodeLocation> codeLocations = bomTool.extractDetectCodeLocations(detectProject)
                    if (codeLocations != null && codeLocations.size() > 0) {
                        bomToolSummaryResults.put(bomTool.getBomToolType(), Result.SUCCESS)
                        detectProject.addAllDetectCodeLocations(codeLocations)
                    } else {
                        logger.error("Did not find any projects from ${bomToolTypeString} even though it applied.")
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
        detectProject.setProjectName(getProjectName(detectProject.projectName))
        detectProject.setProjectVersionName(getProjectVersionName(detectProject.projectVersionName))

        if (!foundAnyBomTools) {
            logger.info("No package managers were detected - will register ${detectConfiguration.sourcePath} for signature scanning of ${detectProject.projectName}/${detectProject.projectVersionName}")
            hubSignatureScanner.registerPathToScan(detectConfiguration.sourceDirectory)
        }

        detectProject
    }

    public List<File> createBdioFiles(DetectProject detectProject) {
        List<File> bdioFiles = []
        MutableDependencyGraph aggregateDependencyGraph = simpleBdioFactory.createMutableDependencyGraph()

        Map<ExternalId, BdioNode> nodeMap = new HashMap<ExternalId, BdioNode>()
        detectProject.detectCodeLocations.each {
            if (detectConfiguration.aggregateBomName) {
                aggregateDependencyGraph.addGraphAsChildrenToRoot(it.dependencyGraph)
            } else {
                if (it.dependencyGraph == null || it.dependencyGraph.getRootDependencies().size() <= 0) {
                    logger.warn("Could not find any dependencies for code location ${it.sourcePath}")
                }

                String projectName = detectProject.getProjectName()
                String projectVersionName = detectProject.getProjectVersionName()

                String prefix = detectConfiguration.getProjectCodeLocationPrefix()
                String suffix = detectConfiguration.getProjectCodeLocationSuffix()

                CodeLocationName codeLocationName = codeLocationNameService.createBomToolName(it.sourcePath, projectName, projectVersionName, it.bomToolType, prefix, suffix)
                String codeLocationNameString = codeLocationNameService.generateBomToolCurrent(codeLocationName)
                final SimpleBdioDocument simpleBdioDocument = createSimpleBdioDocument(codeLocationNameString, detectProject, it)

                String finalSourcePathPiece = detectFileManager.extractFinalPieceFromPath(it.sourcePath);
                final String filename = generateShortenedFilename(it.bomToolType, finalSourcePathPiece, it.getBomToolProjectExternalId());

                final File outputFile = new File(detectConfiguration.bdioOutputDirectoryPath, filename)
                if (outputFile.exists()) {
                    outputFile.delete()
                }
                simpleBdioFactory.writeSimpleBdioDocumentToFile(outputFile, gson, simpleBdioDocument)
                bdioFiles.add(outputFile)
                logger.info("BDIO Generated: " + outputFile.getAbsolutePath())
            }
        }

        if (detectConfiguration.aggregateBomName) {
            SimpleBdioDocument aggregateBdioDocument = createAggregateSimpleBdioDocument(detectProject, aggregateDependencyGraph)
            final String filename = "${integrationEscapeUtil.escapeForUri(detectConfiguration.aggregateBomName)}.jsonld"
            File aggregateBdioFile = new File(detectConfiguration.getOutputDirectory(), filename)
            if (aggregateBdioFile.exists()) {
                aggregateBdioFile.delete()
            }
            simpleBdioFactory.writeSimpleBdioDocumentToFile(aggregateBdioFile, gson, aggregateBdioDocument)
            logger.info("BDIO Generated: " + aggregateBdioFile.getAbsolutePath())
        }

        bdioFiles
    }

    @Override
    public List<BomToolSummaryResult> getDetectSummaryResults() {
        List<BomToolSummaryResult> detectSummaryResults = new ArrayList<>();
        for (Map.Entry<BomToolType, Result> entry : bomToolSummaryResults.entrySet()) {
            detectSummaryResults.add(new BomToolSummaryResult(entry.getKey(), entry.getValue()));
        }
        return detectSummaryResults;
    }

    private String generateShortenedFilename(BomToolType bomToolType, String finalSourcePathPiece, ExternalId externalId) {
        List<String> filenamePieces = new ArrayList<>();
        filenamePieces.addAll(externalId.getExternalIdPieces());
        filenamePieces.add(finalSourcePathPiece);
        String filename = generateFilename(bomToolType, filenamePieces)

        if (filename.length() >= 255){
            filenamePieces.sort { it.size() }
            for (int i = filenamePieces.size() - 1; (filename.length() >= 255) && (i >= 0); i--) {
                filenamePieces[i] = DigestUtils.sha1Hex(filenamePieces[i])
                if (filenamePieces[i].length() > 15) {
                    filenamePieces[i] = filenamePieces[i].substring(0, 15)
                }

                filename = generateFilename(bomToolType, filenamePieces)
            }
        }

        filename
    }

    private String generateFilename(BomToolType bomToolType, List<String> pieces) {
        List<String> rawPieces = new ArrayList<String>();
        rawPieces.add(bomToolType.toString());
        rawPieces.addAll(pieces);
        rawPieces.add('bdio');

        List<String> safePieces = rawPieces.collect { integrationEscapeUtil.escapeForUri(it) };

        String filename = (safePieces as Iterable).join('_') + '.jsonld';

        filename;
    }

    private SimpleBdioDocument createAggregateSimpleBdioDocument(DetectProject detectProject, DependencyGraph dependencyGraph) {
        final String codeLocationName = ''
        final String projectName = detectProject.getProjectName()
        final String projectVersionName = detectProject.projectVersionName
        final ExternalId projectExternalId = simpleBdioFactory.createNameVersionExternalId(new Forge('', '/'), projectName, projectVersionName)

        createSimpleBdioDocument(codeLocationName, projectName, projectVersionName, projectExternalId, dependencyGraph)
    }

    private SimpleBdioDocument createSimpleBdioDocument(String codeLocationName, DetectProject detectProject, DetectCodeLocation detectCodeLocation) {
        final String projectName = detectProject.projectName
        final String projectVersionName = detectProject.projectVersionName
        final ExternalId projectExternalId = detectCodeLocation.bomToolProjectExternalId
        final DependencyGraph dependencyGraph = detectCodeLocation.dependencyGraph

        createSimpleBdioDocument(codeLocationName, projectName, projectVersionName, projectExternalId, dependencyGraph)
    }

    private SimpleBdioDocument createSimpleBdioDocument(String codeLocationName, String projectName, String projectVersionName, ExternalId projectExternalId, DependencyGraph dependencyGraph) {
        SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createSimpleBdioDocument(codeLocationName, projectName, projectVersionName, projectExternalId, dependencyGraph)

        String hubDetectVersion = detectInfo.detectVersion
        def detectVersionData = ['detectVersion' : hubDetectVersion]
        simpleBdioDocument.billOfMaterials.customData = detectVersionData

        simpleBdioDocument

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
