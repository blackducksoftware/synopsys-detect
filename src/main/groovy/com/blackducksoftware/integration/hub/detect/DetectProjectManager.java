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
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.bdio.SimpleBdioFactory;
import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.SimpleBdioDocument;
import com.blackducksoftware.integration.hub.bdio.model.ToolSpdxCreator;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.detect.bomtool.search.BomToolFinder;
import com.blackducksoftware.integration.hub.detect.bomtool.search.BomToolFinderOptions;
import com.blackducksoftware.integration.hub.detect.bomtool.search.report.ExtractionReporter;
import com.blackducksoftware.integration.hub.detect.bomtool.search.report.ExtractionSummaryReporter;
import com.blackducksoftware.integration.hub.detect.bomtool.search.report.PreparationSummaryReporter;
import com.blackducksoftware.integration.hub.detect.bomtool.search.report.SearchSummaryReporter;
import com.blackducksoftware.integration.hub.detect.codelocation.BomCodeLocationNameFactory;
import com.blackducksoftware.integration.hub.detect.codelocation.DockerCodeLocationNameFactory;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeReporter;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.extraction.StrategyEvaluation;
import com.blackducksoftware.integration.hub.detect.hub.HubSignatureScanner;
import com.blackducksoftware.integration.hub.detect.hub.ScanPathSource;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.model.DetectProject;
import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.strategy.StrategyManager;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyException;
import com.blackducksoftware.integration.hub.detect.strategy.result.ExceptionStrategyResult;
import com.blackducksoftware.integration.hub.detect.summary.BomToolSummaryResult;
import com.blackducksoftware.integration.hub.detect.summary.Result;
import com.blackducksoftware.integration.hub.detect.summary.SummaryResultReporter;
import com.blackducksoftware.integration.hub.detect.util.BdioFileNamer;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.util.ExcludedIncludedFilter;
import com.blackducksoftware.integration.util.IntegrationEscapeUtil;

@Component
public class DetectProjectManager implements SummaryResultReporter, ExitCodeReporter {
    private final Logger logger = LoggerFactory.getLogger(DetectProjectManager.class);
    private final Map<BomToolType, Result> bomToolSummaryResults = new HashMap<>();
    private ExitCodeType bomToolSearchExitCodeType;

    @Autowired
    private DetectInfo detectInfo;

    @Autowired
    private DetectConfiguration detectConfiguration;

    @Autowired
    private SimpleBdioFactory simpleBdioFactory;

    @Autowired
    private HubSignatureScanner hubSignatureScanner;

    @Autowired
    private IntegrationEscapeUtil integrationEscapeUtil;

    @Autowired
    private BdioFileNamer bdioFileNamer;

    @Autowired
    private DetectFileFinder detectFileFinder;

    @Autowired
    private DetectPhoneHomeManager detectPhoneHomeManager;

    @Autowired
    public StrategyManager strategyManager;

    @Autowired
    public List<Extractor> autowiredExtractors;

    private boolean foundAnyBomTools;

    private  void extract(final List<StrategyEvaluation> results) {
        final List<StrategyEvaluation> extractable = results.stream().filter(result -> result.isExtractable()).collect(Collectors.toList());

        for (int i = 0; i < extractable.size(); i++) {
            logger.info("Extracting " + Integer.toString(i) + " of " + Integer.toString(extractable.size()) + " (" + Integer.toString((int)Math.floor((i * 100.0f) / extractable.size())) + "%)");
            extract(extractable.get(i));
        }
    }

    private void prepare(final List<StrategyEvaluation> results) {
        for (final StrategyEvaluation result : results) {
            prepare(result);
        }
    }

    private void prepare(final StrategyEvaluation result) {
        if (result.isApplicable()) {
            try {
                result.extractable = result.strategy.extractable(result.environment, result.context);
            } catch (final StrategyException e) {
                result.extractable = new ExceptionStrategyResult(e);
            }
        }
    }

    private void extract(final StrategyEvaluation result) {
        if (result.isExtractable()) {
            extractionReporter.startedExtraction(result.strategy, result.context);
            result.extraction = execute(result.strategy, result.context);
            extractionReporter.endedExtraction(result.extraction);
        }

    }

    public Extraction execute(final Strategy strategy, final ExtractionContext context) {
        Extractor extractor = null;
        for (final Extractor possibleExtractor : autowiredExtractors) {
            if (possibleExtractor.getClass().equals(strategy.getExtractorClass())) {
                extractor = possibleExtractor;
            }
        }

        Extraction result;
        try {
            result = extractor.extract(context);
        } catch (final Exception e) {
            result = new Extraction.Builder().exception(e).build();
        }
        return result;
    }

    private List<StrategyEvaluation> findRootApplicable(final File directory) {
        final List<Strategy> allStrategies = strategyManager.getAllStrategies();
        final List<String> excludedDirectories = detectConfiguration.getBomToolSearchDirectoryExclusions();
        final Boolean forceNestedSearch = detectConfiguration.getBomToolContinueSearch();
        final int maxDepth = detectConfiguration.getBomToolSearchDepth();
        final ExcludedIncludedFilter bomToolFilter = new ExcludedIncludedFilter(detectConfiguration.getExcludedBomToolTypes(), detectConfiguration.getIncludedBomToolTypes());
        final BomToolFinderOptions findOptions = new BomToolFinderOptions(excludedDirectories, forceNestedSearch, maxDepth, bomToolFilter);

        try {
            final BomToolFinder bomToolTreeWalker = new BomToolFinder();
            return bomToolTreeWalker.findApplicableBomTools(new HashSet<>(allStrategies), directory, findOptions);
        } catch (final BomToolException e) {
            bomToolSearchExitCodeType = ExitCodeType.FAILURE_BOM_TOOL;
            logger.error(e.getMessage(), e);
        } catch (final DetectUserFriendlyException e) {
            bomToolSearchExitCodeType = e.getExitCodeType();
            logger.error(e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    @Autowired
    SearchSummaryReporter searchSummaryReporter;

    @Autowired
    PreparationSummaryReporter preparationSummaryReporter;

    @Autowired
    ExtractionSummaryReporter extractionSummaryReporter;

    @Autowired
    ExtractionReporter extractionReporter;

    @Autowired
    private BomCodeLocationNameFactory bomCodeLocationNameFactory;

    @Autowired
    private DockerCodeLocationNameFactory dockerCodeLocationNameFactory;


    public DetectProject createDetectProject() throws IntegrationException, DetectUserFriendlyException {
        final DetectProject detectProject = new DetectProject();

        final List<StrategyEvaluation> sourcePathResults = findRootApplicable(new File(detectConfiguration.getSourcePath()));

        searchSummaryReporter.print(sourcePathResults);

        final float appliedNotInSourceDirectory = sourcePathResults.stream()
                .filter(it -> it.isApplicable())
                .filter(it -> it.environment.getDepth() > 0)
                .count();

        if (appliedNotInSourceDirectory > 1) {
            if (StringUtils.isBlank(detectConfiguration.getProjectName())) {
                throw new DetectUserFriendlyException("Multiple bom tool types applied but no project name was supplied. Detect is unable to reasonably guess the project name and version. Please provide a project name and version with --detect.project.name and --detect.project.version", ExitCodeType.FAILURE_CONFIGURATION);
            } else if (StringUtils.isBlank(detectConfiguration.getProjectVersionName())) {
                throw new DetectUserFriendlyException("Multiple bom tool types applied but no project version was supplied. Detect is unable to reasonably guess the project version. Please provide a project name with --detect.project.version", ExitCodeType.FAILURE_CONFIGURATION);
            }else {
                detectProject.setProjectNameIfNotSet(detectConfiguration.getProjectName());
                detectProject.setProjectVersionNameIfNotSet(detectConfiguration.getProjectVersionName());
            }
        }

        final Set<BomToolType> applicableBomTools = sourcePathResults.stream()
                .filter(it -> it.isApplicable())
                .map(it -> it.strategy.getBomToolType())
                .collect(Collectors.toSet());

        // we've gone through all applicable bom tools so we now have the complete metadata to phone home
        detectPhoneHomeManager.startPhoneHome(applicableBomTools);

        prepare(sourcePathResults);

        preparationSummaryReporter.print(sourcePathResults);

        extract(sourcePathResults);


        final float appliedInSource = sourcePathResults.stream()
                .filter(it -> it.isApplicable())
                .filter(it -> it.environment.getDepth() == 0)
                .count();

        if (appliedInSource > 1) {
            //take the first project alphabetically.
            final Optional<StrategyEvaluation> projectNameDecider = sourcePathResults.stream()
                    .filter(it -> it.isExtractionSuccess() && it.environment.getDepth() == 0 && it.extraction.projectName != null)
                    .sorted((o1, o2) -> o1.extraction.projectName.compareTo(o2.extraction.projectName))
                    .findFirst();

            if (projectNameDecider.isPresent()) {
                detectProject.setProjectNameIfNotSet(projectNameDecider.get().extraction.projectName);
                detectProject.setProjectVersionNameIfNotSet(projectNameDecider.get().extraction.projectVersion);
            }
        }

        final String prefix = detectConfiguration.getProjectCodeLocationPrefix();
        final String suffix = detectConfiguration.getProjectCodeLocationSuffix();

        // ensure that the project name is set, use some reasonable defaults
        detectProject.setProjectDetails(getProjectName(detectProject.getProjectName()), getProjectVersionName(detectProject.getProjectVersionName()), prefix, suffix);

        if (!foundAnyBomTools) {
            logger.info(String.format("No package managers were detected - will register %s for signature scanning of %s/%s", detectConfiguration.getSourcePath(), detectProject.getProjectName(), detectProject.getProjectVersionName()));
            hubSignatureScanner.registerPathToScan(ScanPathSource.DETECT_SOURCE, detectConfiguration.getSourceDirectory());
        } else if (detectConfiguration.getHubSignatureScannerSnippetMode()) {
            logger.info(String.format("Snippet mode is enabled - will register %s for signature scanning of %s/%s", detectConfiguration.getSourcePath(), detectProject.getProjectName(), detectProject.getProjectVersionName()));
            hubSignatureScanner.registerPathToScan(ScanPathSource.SNIPPET_SOURCE, detectConfiguration.getSourceDirectory());
        }

        if (StringUtils.isBlank(detectConfiguration.getAggregateBomName())) {
            detectProject.processDetectCodeLocations(bomCodeLocationNameFactory, dockerCodeLocationNameFactory, logger, detectFileFinder,  detectConfiguration.getSourceDirectory(), bdioFileNamer);

            for (final BomToolType bomToolType : detectProject.getFailedBomTools()) {
                bomToolSummaryResults.put(bomToolType, Result.FAILURE);
            }

            extractionSummaryReporter.print(sourcePathResults, detectProject);
        }

        return detectProject;
    }

    public List<File> createBdioFiles(final DetectProject detectProject) throws DetectUserFriendlyException {
        final List<File> bdioFiles = new ArrayList<>();
        final MutableDependencyGraph aggregateDependencyGraph = simpleBdioFactory.createMutableDependencyGraph();

        if (StringUtils.isBlank(detectConfiguration.getAggregateBomName())) {
            for (final String codeLocationNameString : detectProject.getCodeLocationNameStrings()) {
                final DetectCodeLocation detectCodeLocation = detectProject.getDetectCodeLocation(codeLocationNameString);
                final String bdioFileName = detectProject.getBdioFilename(codeLocationNameString);
                final SimpleBdioDocument simpleBdioDocument = createSimpleBdioDocument(codeLocationNameString, detectProject.getProjectName(), detectProject.getProjectVersionName(), detectCodeLocation);

                final File outputFile = new File(detectConfiguration.getBdioOutputDirectoryPath(), bdioFileName);
                if (outputFile.exists()) {
                    final boolean deleteSuccess = outputFile.delete();
                    logger.debug(String.format("%s deleted: %b", outputFile.getAbsolutePath(), deleteSuccess));
                }
                writeBdioFile(outputFile, simpleBdioDocument);
                bdioFiles.add(outputFile);
            }
        } else {
            for (final DetectCodeLocation detectCodeLocation : detectProject.getDetectCodeLocations()) {
                if (detectCodeLocation.getDependencyGraph() == null) {
                    logger.warn(String.format("Dependency graph is null for code location %s", detectCodeLocation.getSourcePath()));
                    continue;
                }
                if (detectCodeLocation.getDependencyGraph().getRootDependencies().size() <= 0) {
                    logger.warn(String.format("Could not find any dependencies for code location %s", detectCodeLocation.getSourcePath()));
                }
                aggregateDependencyGraph.addGraphAsChildrenToRoot(detectCodeLocation.getDependencyGraph());
            }
            final SimpleBdioDocument aggregateBdioDocument = createAggregateSimpleBdioDocument(detectProject.getProjectName(), detectProject.getProjectVersionName(), aggregateDependencyGraph);
            final String filename = String.format("%s.jsonld", integrationEscapeUtil.escapeForUri(detectConfiguration.getAggregateBomName()));
            final File aggregateBdioFile = new File(detectConfiguration.getOutputDirectory(), filename);
            if (aggregateBdioFile.exists()) {
                final boolean deleteSuccess = aggregateBdioFile.delete();
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
        if (null != bomToolSearchExitCodeType) {
            return bomToolSearchExitCodeType;
        }
        return ExitCodeType.SUCCESS;
    }

    private SimpleBdioDocument createAggregateSimpleBdioDocument(final String projectName, final String projectVersionName, final DependencyGraph dependencyGraph) {
        final String codeLocationName = "";
        final ExternalId projectExternalId = simpleBdioFactory.createNameVersionExternalId(new Forge("/", "/", ""), projectName, projectVersionName);

        return createSimpleBdioDocument(codeLocationName, projectName, projectVersionName, projectExternalId, dependencyGraph);
    }

    private String getProjectName(final String defaultProjectName) {
        String projectName = null;
        if (null != defaultProjectName) {
            projectName = defaultProjectName.trim();
        }
        if (StringUtils.isNotBlank(detectConfiguration.getProjectName())) {
            projectName = detectConfiguration.getProjectName();
        } else if (StringUtils.isBlank(projectName) && StringUtils.isNotBlank(detectConfiguration.getSourcePath())) {
            final String finalSourcePathPiece = detectFileFinder.extractFinalPieceFromPath(detectConfiguration.getSourcePath());
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
                final String timeString = DateTimeFormatter.ofPattern(timeformat).withZone(ZoneOffset.UTC).format(Instant.now().atZone(ZoneOffset.UTC));
                projectVersion = timeString;
            } else {
                projectVersion = detectConfiguration.getDefaultProjectVersionText();
            }
        }

        return projectVersion;
    }

    private SimpleBdioDocument createSimpleBdioDocument(final String codeLocationName, final String projectName, final String projectVersionName, final DetectCodeLocation detectCodeLocation) {
        final ExternalId projectExternalId = detectCodeLocation.getBomToolProjectExternalId();
        final DependencyGraph dependencyGraph = detectCodeLocation.getDependencyGraph();

        return createSimpleBdioDocument(codeLocationName, projectName, projectVersionName, projectExternalId, dependencyGraph);
    }

    private SimpleBdioDocument createSimpleBdioDocument(final String codeLocationName, final String projectName, final String projectVersionName, final ExternalId projectExternalId, final DependencyGraph dependencyGraph) {
        final SimpleBdioDocument simpleBdioDocument = simpleBdioFactory.createSimpleBdioDocument(codeLocationName, projectName, projectVersionName, projectExternalId, dependencyGraph);

        final String hubDetectVersion = detectInfo.getDetectVersion();
        final ToolSpdxCreator hubDetectCreator = new ToolSpdxCreator("HubDetect", hubDetectVersion);
        simpleBdioDocument.billOfMaterials.creationInfo.addSpdxCreator(hubDetectCreator);

        return simpleBdioDocument;
    }

}
