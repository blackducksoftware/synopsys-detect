/**
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
package com.blackducksoftware.integration.hub.detect;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.SimpleBdioFactory;
import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.SimpleBdioDocument;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.codelocation.CodeLocationName;
import com.blackducksoftware.integration.hub.detect.codelocation.CodeLocationNameService;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeReporter;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.hub.HubSignatureScanner;
import com.blackducksoftware.integration.hub.detect.hub.ScanPathSource;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.model.DetectProject;
import com.blackducksoftware.integration.hub.detect.summary.BomToolSummaryResult;
import com.blackducksoftware.integration.hub.detect.summary.Result;
import com.blackducksoftware.integration.hub.detect.summary.SummaryResultReporter;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.util.IntegrationEscapeUtil;

import groovy.transform.TypeChecked;

@Component
@TypeChecked
public class DetectProjectManager implements SummaryResultReporter, ExitCodeReporter {
    private final Logger logger = LoggerFactory.getLogger(DetectProjectManager.class);

    @Autowired
    private DetectInfo detectInfo;

    @Autowired
    private DetectConfiguration detectConfiguration;

    @Autowired
    private SimpleBdioFactory simpleBdioFactory;

    @Autowired
    private List<BomTool> bomTools;

    @Autowired
    private HubSignatureScanner hubSignatureScanner;

    @Autowired
    private IntegrationEscapeUtil integrationEscapeUtil;

    @Autowired
    private DetectFileManager detectFileManager;

    @Autowired
    private CodeLocationNameService codeLocationNameService;

    @Autowired
    private DetectPhoneHomeManager detectPhoneHomeManager;

    private boolean foundAnyBomTools;

    private final Map<BomToolType, Result> bomToolSummaryResults = new HashMap<>();

    public DetectProject createDetectProject() {
        final DetectProject detectProject = new DetectProject();

        final EnumSet<BomToolType> applicableBomTools = EnumSet.noneOf(BomToolType.class);
        for (final BomTool bomTool : bomTools) {
            final BomToolType bomToolType = bomTool.getBomToolType();
            final String bomToolTypeString = bomToolType.toString();
            try {
                if (!detectConfiguration.shouldRun(bomTool)) {
                    logger.debug(String.format("Skipping %s.", bomToolTypeString));
                    continue;
                }

                logger.info(String.format("%s applies given the current configuration.", bomToolTypeString));
                bomToolSummaryResults.put(bomTool.getBomToolType(), Result.FAILURE);
                foundAnyBomTools = true;
                final List<DetectCodeLocation> codeLocations = bomTool.extractDetectCodeLocations(detectProject);
                if (codeLocations != null && codeLocations.size() > 0) {
                    bomToolSummaryResults.put(bomTool.getBomToolType(), Result.SUCCESS);
                    detectProject.addAllDetectCodeLocations(codeLocations);
                    applicableBomTools.add(bomToolType);
                } else {
                    logger.error(String.format("Did not find any projects from %s even though it applied.", bomToolTypeString));
                }
            } catch (final Exception e) {
                // any bom tool failure should not prevent other bom tools from running
                logger.error(String.format("%s threw an Exception: %s", bomToolTypeString, e.getMessage()));

                // log the stacktrace if and only if running at trace level
                if (logger.isTraceEnabled()) {
                    logger.error("Exception details: ", e);
                }
            }
        }

        // we've gone through all applicable bom tools so we now have the
        // complete metadata to phone home
        detectPhoneHomeManager.startPhoneHome(applicableBomTools);

        // ensure that the project name is set, use some reasonable defaults
        detectProject.setProjectName(getProjectName(detectProject.getProjectName()));
        detectProject.setProjectVersionName(getProjectVersionName(detectProject.getProjectVersionName()));

        if (!foundAnyBomTools) {
            logger.info(String.format("No package managers were detected - will register %s for signature scanning of %s/%s", detectConfiguration.getSourcePath(), detectProject.getProjectName(), detectProject.getProjectVersionName()));
            hubSignatureScanner.registerPathToScan(ScanPathSource.DETECT_SOURCE, detectConfiguration.getSourceDirectory());
        } else if (detectConfiguration.getHubSignatureScannerSnippetMode()) {
            logger.info(String.format("Snippet mode is enabled - will register %s for signature scanning of %s/%s", detectConfiguration.getSourcePath(), detectProject.getProjectName(), detectProject.getProjectVersionName()));
            hubSignatureScanner.registerPathToScan(ScanPathSource.SNIPPET_SOURCE, detectConfiguration.getSourceDirectory());
        }

        return detectProject;
    }

    public List<File> createBdioFiles(final DetectProject detectProject) throws DetectUserFriendlyException {
        final List<File> bdioFiles = new ArrayList<>();
        final MutableDependencyGraph aggregateDependencyGraph = simpleBdioFactory.createMutableDependencyGraph();

        final Set<String> codeLocationNames = new HashSet<>();
        final Set<String> bdioFileNames = new HashSet<>();

        for (final DetectCodeLocation detectCodeLocation : detectProject.getDetectCodeLocations()) {
            if (detectCodeLocation.getDependencyGraph() == null) {
                logger.warn(String.format("Dependency graph is null for code location %s", detectCodeLocation.getSourcePath()));
                continue;
            }
            if (detectCodeLocation.getDependencyGraph().getRootDependencies().size() <= 0) {
                logger.warn(String.format("Could not find any dependencies for code location %s", detectCodeLocation.getSourcePath()));
            }
            if (StringUtils.isNotBlank(detectConfiguration.getAggregateBomName())) {
                aggregateDependencyGraph.addGraphAsChildrenToRoot(detectCodeLocation.getDependencyGraph());
            } else {
                final String projectName = detectProject.getProjectName();
                final String projectVersionName = detectProject.getProjectVersionName();

                final String prefix = detectConfiguration.getProjectCodeLocationPrefix();
                final String suffix = detectConfiguration.getProjectCodeLocationSuffix();

                final CodeLocationName codeLocationName = detectCodeLocation.createCodeLocationName(codeLocationNameService, projectName, projectVersionName, prefix, suffix);
                final String codeLocationNameString = detectCodeLocation.getCodeLocationNameString(codeLocationNameService, codeLocationName);
                if (!codeLocationNames.add(codeLocationNameString)) {
                    throw new DetectUserFriendlyException(String.format("Found duplicate Code Locations with the name: %s", codeLocationNameString), ExitCodeType.FAILURE_GENERAL_ERROR);
                }
                final SimpleBdioDocument simpleBdioDocument = createSimpleBdioDocument(codeLocationNameString, detectProject, detectCodeLocation);

                final String finalSourcePathPiece = detectFileManager.extractFinalPieceFromPath(detectCodeLocation.getSourcePath());
                final String filename = generateShortenedFilename(detectCodeLocation.getBomToolType(), finalSourcePathPiece, detectCodeLocation.getBomToolProjectExternalId());
                if (!bdioFileNames.add(filename)) {
                    throw new DetectUserFriendlyException(String.format("Found duplicate Bdio files with the name: %s", filename), ExitCodeType.FAILURE_GENERAL_ERROR);
                }
                final File outputFile = new File(detectConfiguration.getBdioOutputDirectoryPath(), filename);
                if (outputFile.exists()) {
                    boolean deleteSuccess = outputFile.delete();
                    logger.debug(String.format("%s deleted: %b", outputFile.getAbsolutePath(), deleteSuccess));
                }
                writeBdioFile(outputFile, simpleBdioDocument);
                bdioFiles.add(outputFile);
            }
        }

        if (StringUtils.isNotBlank(detectConfiguration.getAggregateBomName())) {
            final SimpleBdioDocument aggregateBdioDocument = createAggregateSimpleBdioDocument(detectProject, aggregateDependencyGraph);
            final String filename = String.format("%s.jsonld", integrationEscapeUtil.escapeForUri(detectConfiguration.getAggregateBomName()));
            final File aggregateBdioFile = new File(detectConfiguration.getOutputDirectory(), filename);
            if (aggregateBdioFile.exists()) {
                boolean deleteSuccess = aggregateBdioFile.delete();
                logger.debug(String.format("%s deleted: %b", aggregateBdioFile.getAbsolutePath(), deleteSuccess));
            }
            writeBdioFile(aggregateBdioFile, aggregateBdioDocument);
        }

        return bdioFiles;
    }

    private void writeBdioFile(final File outputFile, final SimpleBdioDocument simpleBdioDocument) throws DetectUserFriendlyException {
        try {
            simpleBdioFactory.writeSimpleBdioDocumentToFile(outputFile, simpleBdioDocument);
            logger.info(String.format("BDIO Generated: %s", outputFile.getAbsolutePath()));
        } catch (final IOException e) {
            throw new DetectUserFriendlyException(e.getMessage(), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }

    @Override
    public List<BomToolSummaryResult> getDetectSummaryResults() {
        final List<BomToolSummaryResult> detectSummaryResults = new ArrayList<>();
        for (final Map.Entry<BomToolType, Result> entry : bomToolSummaryResults.entrySet()) {
            detectSummaryResults.add(new BomToolSummaryResult(entry.getKey(), entry.getValue()));
        }
        return detectSummaryResults;
    }

    @Override
    public ExitCodeType getExitCodeType() {
        for (final Map.Entry<BomToolType, Result> entry : bomToolSummaryResults.entrySet()) {
            if (Result.FAILURE == entry.getValue()) {
                return ExitCodeType.FAILURE_BOM_TOOL;
            }
        }
        return ExitCodeType.SUCCESS;
    }

    private String generateShortenedFilename(final BomToolType bomToolType, final String finalSourcePathPiece, final ExternalId externalId) {
        final List<String> filenamePieces = new ArrayList<>();
        filenamePieces.addAll(Arrays.asList(externalId.getExternalIdPieces()));
        filenamePieces.add(finalSourcePathPiece);
        String filename = generateFilename(bomToolType, filenamePieces);

        if (filename.length() >= 255) {
            filenamePieces.sort(new Comparator<String>() {
                @Override
                public int compare(final String s1, final String s2) {
                    return s1.length() - s2.length();
                }
            });
            for (int i = filenamePieces.size() - 1; (filename.length() >= 255) && (i >= 0); i--) {
                filenamePieces.set(i, DigestUtils.sha1Hex(filenamePieces.get(i)));
                if (filenamePieces.get(i).length() > 15) {
                    filenamePieces.set(i, filenamePieces.get(i).substring(0, 15));
                }
                filename = generateFilename(bomToolType, filenamePieces);
            }
        }

        return filename;
    }

    private String generateFilename(final BomToolType bomToolType, final List<String> pieces) {
        final List<String> rawPieces = new ArrayList<>();
        rawPieces.add(bomToolType.toString());
        rawPieces.addAll(pieces);
        rawPieces.add("bdio");

        final List<String> safePieces = new ArrayList<>();
        for (final String rawPiece : rawPieces) {
            safePieces.add(integrationEscapeUtil.escapeForUri(rawPiece));
        }
        final String filename = StringUtils.join(safePieces, "_") + ".jsonld";
        return filename;
    }

    private SimpleBdioDocument createAggregateSimpleBdioDocument(final DetectProject detectProject, final DependencyGraph dependencyGraph) {
        final String codeLocationName = "";
        final String projectName = detectProject.getProjectName();
        final String projectVersionName = detectProject.getProjectVersionName();
        final ExternalId projectExternalId = simpleBdioFactory.createNameVersionExternalId(new Forge("/", "/", ""), projectName, projectVersionName);

        return createSimpleBdioDocument(codeLocationName, projectName, projectVersionName, projectExternalId, dependencyGraph);
    }

    private SimpleBdioDocument createSimpleBdioDocument(final String codeLocationName, final DetectProject detectProject, final DetectCodeLocation detectCodeLocation) {
        final String projectName = detectProject.getProjectName();
        final String projectVersionName = detectProject.getProjectVersionName();
        final ExternalId projectExternalId = detectCodeLocation.getBomToolProjectExternalId();
        final DependencyGraph dependencyGraph = detectCodeLocation.getDependencyGraph();

        return createSimpleBdioDocument(codeLocationName, projectName, projectVersionName, projectExternalId, dependencyGraph);
    }

    private SimpleBdioDocument createSimpleBdioDocument(final String codeLocationName, final String projectName, final String projectVersionName, final ExternalId projectExternalId, final DependencyGraph dependencyGraph) {
        final SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createSimpleBdioDocument(codeLocationName, projectName, projectVersionName, projectExternalId, dependencyGraph);

        final String hubDetectVersion = detectInfo.getDetectVersion();
        final Map<String, String> detectVersionData = new HashMap<>();
        detectVersionData.put("detectVersion", hubDetectVersion);
        simpleBdioDocument.billOfMaterials.customData = detectVersionData;

        return simpleBdioDocument;
    }

    private String getProjectName(final String defaultProjectName) {
        String projectName = null;
        if (null != defaultProjectName) {
            projectName = defaultProjectName.trim();
        }
        if (StringUtils.isNotBlank(detectConfiguration.getProjectName())) {
            projectName = detectConfiguration.getProjectName();
        } else if (StringUtils.isBlank(projectName) && StringUtils.isNotBlank(detectConfiguration.getSourcePath())) {
            final String finalSourcePathPiece = detectFileManager.extractFinalPieceFromPath(detectConfiguration.getSourcePath());
            projectName = finalSourcePathPiece;
        }
        return projectName;
    }

    private String getProjectVersionName(final String defaultVersionName) {
        String projectVersion = null;
        if (null != defaultVersionName) {
            projectVersion = defaultVersionName.trim();
        }

        if (StringUtils.isNotBlank(detectConfiguration.getProjectVersionName())) {
            projectVersion = detectConfiguration.getProjectVersionName();
        } else if (StringUtils.isBlank(projectVersion)) {
            if ("timestamp".equals(detectConfiguration.getDefaultProjectVersionScheme())) {
                final String timeformat = detectConfiguration.getDefaultProjectVersionTimeformat();
                final String timeString = DateTimeFormat.forPattern(timeformat).withZoneUTC().print(DateTime.now().withZone(DateTimeZone.UTC));
                projectVersion = timeString;
            } else {
                projectVersion = detectConfiguration.getDefaultProjectVersionText();
            }
        }

        return projectVersion;
    }

}
