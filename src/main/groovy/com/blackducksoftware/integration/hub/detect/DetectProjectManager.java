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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
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
import com.blackducksoftware.integration.hub.detect.bomtool.search.StrategyFindResult;
import com.blackducksoftware.integration.hub.detect.bomtool.search.StrategyFindResult.FindType;
import com.blackducksoftware.integration.hub.detect.bomtool.search.report.ExtractionReporter;
import com.blackducksoftware.integration.hub.detect.bomtool.search.report.ExtractionSummaryReporter;
import com.blackducksoftware.integration.hub.detect.bomtool.search.report.PreparationSummaryReporter;
import com.blackducksoftware.integration.hub.detect.bomtool.search.report.SearchSummaryReporter;
import com.blackducksoftware.integration.hub.detect.codelocation.CodeLocationNameService;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeReporter;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation.EvaluationResult;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.StrategyManager;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.evaluation.StrategyEvaluation;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.evaluation.StrategyEvaluator;
import com.blackducksoftware.integration.hub.detect.hub.HubSignatureScanner;
import com.blackducksoftware.integration.hub.detect.hub.ScanPathSource;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.model.DetectProject;
import com.blackducksoftware.integration.hub.detect.summary.BomToolSummaryResult;
import com.blackducksoftware.integration.hub.detect.summary.Result;
import com.blackducksoftware.integration.hub.detect.summary.SummaryResultReporter;
import com.blackducksoftware.integration.hub.detect.util.BdioFileNamer;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
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
    private DetectFileManager detectFileManager;

    @Autowired
    private DetectFileFinder detectFileFinder;

    @Autowired
    private CodeLocationNameService codeLocationNameService;

    @Autowired
    private DetectPhoneHomeManager detectPhoneHomeManager;

    @Autowired
    public StrategyManager strategyManager;

    @Autowired
    public StrategyEvaluator strategyEvaluator;

    private boolean foundAnyBomTools;

    private  void extract(final List<StrategyFindResult> results) {
        final List<StrategyFindResult> extractable = results.stream().filter(result -> {
            if (result.type == FindType.APPLIES) {
                final StrategyEvaluation evaluation = result.evaluation;
                if (evaluation.areNeedsMet() && evaluation.areDemandsMet()) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());

        for (int i = 0; i < extractable.size(); i++) {
            logger.info("Extracting " + Integer.toString(i) + " of " + Integer.toString(extractable.size()) + " (" + Integer.toString((int)Math.floor((i * 100.0f) / extractable.size())) + "%)");
            extract(extractable.get(i));
        }
    }

    private void prepare(final List<StrategyFindResult> results) {
        for (final StrategyFindResult result : results) {
            prepare(result);
        }
    }

    private void prepare(final StrategyFindResult result) {
        if (result.type == FindType.APPLIES) {
            final StrategyEvaluation evaluation = result.evaluation;
            final EvaluationContext context = result.context;
            final Strategy strategy = result.strategy;
            if (evaluation.areNeedsMet()) {
                strategyEvaluator.meetsDemands(evaluation, strategy, context);
            }
        }
    }

    private void extract(final StrategyFindResult result) {
        if (result.type == FindType.APPLIES) {
            final StrategyEvaluation evaluation = result.evaluation;
            if (evaluation.areNeedsMet() && evaluation.areDemandsMet()) {
                final EvaluationContext context = result.context;
                final Strategy strategy = result.strategy;
                final ExtractionContext extractionContext = strategyEvaluator.createContext(evaluation, strategy, context);
                extractionReporter.startedExtraction(strategy, extractionContext);
                final Extraction extraction = strategyEvaluator.execute(strategy, extractionContext);
                evaluation.extraction = extraction;
                extractionReporter.endedExtraction(extraction);
            }

        }
    }

    private void printReqEval(final RequirementEvaluation eval) {
        if (eval.result.equals(EvaluationResult.Exception)) {
            eval.error.printStackTrace();
        } else if (eval.result.equals(EvaluationResult.Failed)) {
            logger.debug(eval.description);
        }
    }


    private List<StrategyFindResult> findRootApplicable(final File directory) {
        final List<Strategy> allStrategies = strategyManager.getAllStrategies();
        final List<String> excludedDirectories = detectConfiguration.getBomToolSearchDirectoryExclusions();
        final Boolean forceNestedSearch = detectConfiguration.getBomToolContinueSearch();
        final int maxDepth = detectConfiguration.getBomToolSearchDepth();
        final BomToolFinderOptions findOptions = new BomToolFinderOptions(excludedDirectories, forceNestedSearch, maxDepth);
        try {
            final BomToolFinder bomToolTreeWalker = new BomToolFinder();
            return bomToolTreeWalker.findApplicableBomTools(new HashSet<>(allStrategies), strategyEvaluator, directory, findOptions);
        } catch (final BomToolException e) {
            bomToolSearchExitCodeType = ExitCodeType.FAILURE_BOM_TOOL;
            logger.error(e.getMessage(), e);
        } catch (final DetectUserFriendlyException e) {
            bomToolSearchExitCodeType = e.getExitCodeType();
            logger.error(e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    private List<StrategyFindResult> findBomTools() {
        final List<Strategy> allStrategies = strategyManager.getAllStrategies();
        final List<String> excludedDirectories = detectConfiguration.getBomToolSearchDirectoryExclusions();
        final Boolean forceNestedSearch = detectConfiguration.getBomToolContinueSearch();
        final int maxDepth = detectConfiguration.getBomToolSearchDepth();
        final BomToolFinderOptions findOptions = new BomToolFinderOptions(excludedDirectories, forceNestedSearch, maxDepth);
        try {
            final File initialDirectory = detectConfiguration.getSourceDirectory();
            final BomToolFinder bomToolTreeWalker = new BomToolFinder();
            return bomToolTreeWalker.findApplicableBomTools(new HashSet<>(allStrategies), strategyEvaluator, initialDirectory, findOptions);
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


    public DetectProject createDetectProject() throws IntegrationException {
        final DetectProject detectProject = new DetectProject();

        final List<StrategyFindResult> sourcePathResults = findRootApplicable(new File(detectConfiguration.getSourcePath()));

        searchSummaryReporter.print(sourcePathResults);

        prepare(sourcePathResults);

        preparationSummaryReporter.print(sourcePathResults);
        //final Set<BomToolType> applicableBomTools = sourcePathResults.stream().map(it -> it.getBomToolType()).collect(Collectors.toSet());
        //        installInspectors(applicableBomTools);

        extract(sourcePathResults);

        extractionSummaryReporter.print(sourcePathResults);

        sourcePathResults.forEach(it -> {
            if (it.type == FindType.APPLIES && it.evaluation.areDemandsMet() && it.evaluation.areNeedsMet() && it.evaluation.extraction.result == ExtractionResult.Success) {
                detectProject.addAllDetectCodeLocations(it.evaluation.extraction.codeLocations);
            }
            //            if (it.extractedCodeLocations.size() > 0) {

            //           } else {
            //              final String bomToolTypeString = it.bomToolType.toString();
            //               logger.error(String.format("Did not find any code locations from %s even though it applied to %s.", bomToolTypeString, it.directory));

        });

        // we've gone through all applicable bom tools so we now have the complete metadata to phone home
        //detectPhoneHomeManager.startPhoneHome(applicableBomTools);

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
            detectProject.processDetectCodeLocations(logger,detectFileFinder,  detectConfiguration.getSourceDirectory(), bdioFileNamer, codeLocationNameService);

            for (final BomToolType bomToolType : detectProject.getFailedBomTools()) {
                bomToolSummaryResults.put(bomToolType, Result.FAILURE);
            }
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
                final String timeString = DateTimeFormat.forPattern(timeformat).withZoneUTC().print(DateTime.now().withZone(DateTimeZone.UTC));
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
