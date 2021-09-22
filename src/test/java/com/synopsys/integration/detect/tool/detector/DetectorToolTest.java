/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detect.tool.detector;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.common.util.finder.SimpleFileFinder;
import com.synopsys.integration.configuration.property.types.enumfilterable.FilterableEnumValue;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.ExcludeIncludeEnumFilter;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodePublisher;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.tool.detector.extraction.ExtractionEnvironmentProvider;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectables.go.gomod.GoModCliDetectable;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.evaluation.DetectorEvaluationOptions;
import com.synopsys.integration.detector.finder.DetectorFinder;
import com.synopsys.integration.detector.finder.DetectorFinderDirectoryListException;
import com.synopsys.integration.detector.finder.DetectorFinderOptions;
import com.synopsys.integration.detector.result.DetectorResult;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.DetectorRuleBuilder;
import com.synopsys.integration.detector.rule.DetectorRuleSet;
import com.synopsys.integration.detector.rule.DetectorRuleSetBuilder;

public class DetectorToolTest {
    @Test
    public void testFailWhenMisConfigured() throws DetectUserFriendlyException {

        ExtractionEnvironmentProvider extractionEnvironmentProvider = Mockito.mock(ExtractionEnvironmentProvider.class);
        DetectorFinder detectorFinder = Mockito.mock(DetectorFinder.class);
        EventSystem eventSystem = Mockito.mock(EventSystem.class);
        CodeLocationConverter codeLocationConverter = Mockito.mock(CodeLocationConverter.class);
        DetectorIssuePublisher detectorIssuePublisher = Mockito.mock(DetectorIssuePublisher.class);
        StatusEventPublisher statusEventPublisher = Mockito.mock(StatusEventPublisher.class);
        ExitCodePublisher exitCodePublisher = Mockito.mock(ExitCodePublisher.class);
        DetectorEventPublisher detectorEventPublisher = Mockito.mock(DetectorEventPublisher.class);

        DetectorTool tool = new DetectorTool(detectorFinder, extractionEnvironmentProvider, eventSystem, codeLocationConverter, detectorIssuePublisher, statusEventPublisher, exitCodePublisher, detectorEventPublisher);

        File directory = new File(".");
        DetectorRuleSet detectorRuleSet = Mockito.mock(DetectorRuleSet.class);
        DetectorFinderOptions detectorFinderOptions = Mockito.mock(DetectorFinderOptions.class);
        DetectorEvaluationOptions evaluationOptions = Mockito.mock(DetectorEvaluationOptions.class);
        String projectBomTool = "testBomTool";

        tool.performDetectors(directory, detectorRuleSet, detectorFinderOptions, evaluationOptions, projectBomTool, new ArrayList<>(), new SimpleFileFinder());

        Mockito.verify(exitCodePublisher).publishExitCode(Mockito.any(ExitCodeType.class), Mockito.anyString());
    }

    @Test
    public void testSuccess() throws DetectUserFriendlyException, DetectorFinderDirectoryListException, DetectableException {
        Extraction extraction = createSuccessExtraction();
        DetectableResult extractionResult = new PassedDetectableResult();
        String projectBomTool = DetectorType.GO_MOD.name();

        DetectorToolResult result = executeToolTest(extraction, extractionResult, projectBomTool);

        assertFalse(result.getApplicableDetectorTypes().isEmpty());
        assertTrue(result.getBomToolCodeLocations().isEmpty());
        assertTrue(result.getBomToolProjectNameVersion().isPresent());
        assertTrue(result.getCodeLocationMap().isEmpty());
        assertTrue(result.getFailedDetectorTypes().isEmpty());
        assertTrue(result.getRootDetectorEvaluationTree().isPresent());
    }

    @Test
    public void testPreferredDetectorMissingSuccess() throws DetectUserFriendlyException, DetectorFinderDirectoryListException, DetectableException {
        Extraction extraction = createSuccessExtraction();
        DetectableResult extractionResult = new PassedDetectableResult();
        String projectBomTool = "testBomTool";

        DetectorToolResult result = executeToolTest(extraction, extractionResult, projectBomTool);

        assertFalse(result.getApplicableDetectorTypes().isEmpty());
        assertTrue(result.getBomToolCodeLocations().isEmpty());
        assertTrue(result.getBomToolProjectNameVersion().isPresent());
        assertTrue(result.getCodeLocationMap().isEmpty());
        assertTrue(result.getFailedDetectorTypes().isEmpty());
        assertTrue(result.getRootDetectorEvaluationTree().isPresent());
    }

    @Test
    public void testExtractionFailed() throws DetectUserFriendlyException, DetectorFinderDirectoryListException, DetectableException {
        Extraction extraction = createFailExtraction();
        DetectableResult extractionResult = new PassedDetectableResult();
        String projectBomTool = DetectorType.GO_MOD.name();

        DetectorToolResult result = executeToolTest(extraction, extractionResult, projectBomTool);
        assertFalse(result.getApplicableDetectorTypes().isEmpty());
        assertTrue(result.getBomToolCodeLocations().isEmpty());
        assertFalse(result.getBomToolProjectNameVersion().isPresent());
        assertTrue(result.getCodeLocationMap().isEmpty());
        assertTrue(result.getFailedDetectorTypes().isEmpty());
        assertTrue(result.getRootDetectorEvaluationTree().isPresent());
    }

    @Test
    public void testExtractionException() throws DetectUserFriendlyException, DetectorFinderDirectoryListException, DetectableException {
        Extraction extraction = createExceptionExtraction();
        DetectableResult extractionResult = new PassedDetectableResult();
        String projectBomTool = DetectorType.GO_MOD.name();

        DetectorToolResult result = executeToolTest(extraction, extractionResult, projectBomTool);
        assertFalse(result.getApplicableDetectorTypes().isEmpty());
        assertTrue(result.getBomToolCodeLocations().isEmpty());
        assertFalse(result.getBomToolProjectNameVersion().isPresent());
        assertTrue(result.getCodeLocationMap().isEmpty());
        assertTrue(result.getFailedDetectorTypes().isEmpty());
        assertTrue(result.getRootDetectorEvaluationTree().isPresent());
    }

    private static class FailureExitCodeRequestMatcher implements ArgumentMatcher<ExitCodeRequest> {
        @Override
        public boolean matches(ExitCodeRequest actualExitCodeRequest) {
            System.out.printf("custom matcher called: %d: %s\n", actualExitCodeRequest.getExitCodeType().getExitCode(), actualExitCodeRequest.getReason());
            return (actualExitCodeRequest.getExitCodeType() == ExitCodeType.FAILURE_CONFIGURATION) && (StringUtils.isNotBlank(actualExitCodeRequest.getReason()));
        }
    }

    private DetectorToolResult executeToolTest(Extraction extraction, DetectableResult extractionResult, String projectBomTool) throws DetectUserFriendlyException, DetectorFinderDirectoryListException, DetectableException {
        ExtractionEnvironmentProvider extractionEnvironmentProvider = Mockito.mock(ExtractionEnvironmentProvider.class);
        DetectorFinder detectorFinder = Mockito.mock(DetectorFinder.class);
        EventSystem eventSystem = Mockito.mock(EventSystem.class);
        CodeLocationConverter codeLocationConverter = Mockito.mock(CodeLocationConverter.class);
        DetectorIssuePublisher detectorIssuePublisher = Mockito.mock(DetectorIssuePublisher.class);
        StatusEventPublisher statusEventPublisher = Mockito.mock(StatusEventPublisher.class);
        ExitCodePublisher exitCodePublisher = Mockito.mock(ExitCodePublisher.class);
        DetectorEventPublisher detectorEventPublisher = Mockito.mock(DetectorEventPublisher.class);

        DetectorTool tool = new DetectorTool(detectorFinder, extractionEnvironmentProvider, eventSystem, codeLocationConverter, detectorIssuePublisher, statusEventPublisher, exitCodePublisher, detectorEventPublisher);
        File directory = new File(".");
        GoModCliDetectable detectable = createDetectable(extraction, extractionResult);
        DetectorRule<GoModCliDetectable> rule = createRule(detectable);
        DetectorRuleSet detectorRuleSet = createRuleSet(rule);
        DetectorFinderOptions detectorFinderOptions = createFinderOptions();
        DetectorEvaluationOptions evaluationOptions = createEvaluationOptions();

        DetectorEvaluationTree evaluationTree = createEvaluationTree(extraction, extractionResult, directory, rule, detectorRuleSet);
        Mockito.when(detectorFinder.findDetectors(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Optional.of(evaluationTree));

        return tool.performDetectors(directory, detectorRuleSet, detectorFinderOptions, evaluationOptions, projectBomTool, new ArrayList<>(), new SimpleFileFinder());
    }

    private GoModCliDetectable createDetectable(Extraction extraction, DetectableResult extractionResult) throws DetectableException {
        File relevantFile = new File("go.mod");
        List<File> relevantFiles = Collections.singletonList(relevantFile);
        GoModCliDetectable detectable = Mockito.mock(GoModCliDetectable.class);
        Mockito.when(detectable.extractable()).thenReturn(extractionResult);

        Mockito.when(detectable.applicable()).thenReturn(new PassedDetectableResult(Collections.emptyList(), relevantFiles));
        Mockito.when(detectable.extract(Mockito.any())).thenReturn(extraction);
        return detectable;
    }

    private Extraction.Builder createExtractionBuilder() {
        File relevantFile = new File("go.mod");
        DependencyGraph dependencyGraph = Mockito.mock(DependencyGraph.class);
        CodeLocation codeLocation = new CodeLocation(dependencyGraph, relevantFile);
        Extraction.Builder builder = new Extraction.Builder();
        return builder.relevantFiles(relevantFile)
                   .codeLocations(codeLocation)
                   .projectName("test-project")
                   .projectVersion("1.0")
                   .unrecognizedPaths(Collections.emptyList());

    }

    private Extraction createSuccessExtraction() {
        return createExtractionBuilder()
                   .success()
                   .build();
    }

    private Extraction createFailExtraction() {
        return createExtractionBuilder()
                   .failure("JUnit extraction failure")
                   .build();
    }

    private Extraction createExceptionExtraction() {
        return createExtractionBuilder()
                   .exception(new RuntimeException("JUnit Extraction Exception"))
                   .build();
    }

    private DetectorRule<GoModCliDetectable> createRule(GoModCliDetectable detectable) {
        DetectorRuleBuilder<GoModCliDetectable> ruleBuilder = new DetectorRuleBuilder<>("GoMod", DetectorType.GO_MOD, GoModCliDetectable.class, (e) -> detectable);
        return ruleBuilder.build();
    }

    private DetectorRuleSet createRuleSet(DetectorRule<GoModCliDetectable> rule) {
        DetectorRuleSetBuilder ruleSetBuilder = new DetectorRuleSetBuilder();
        ruleSetBuilder.add(rule);
        return ruleSetBuilder.build();
    }

    private DetectorFinderOptions createFinderOptions() {
        Predicate<File> fileFilter = f -> true;
        final int maximumDepth = 10;
        return new DetectorFinderOptions(fileFilter, maximumDepth, false);
    }

    private DetectorEvaluationOptions createEvaluationOptions() {
        List<FilterableEnumValue<DetectorType>> excluded = Collections.emptyList();
        List<FilterableEnumValue<DetectorType>> included = Collections.singletonList(FilterableEnumValue.value(DetectorType.GO_MOD));
        ExcludeIncludeEnumFilter detectorFilter = new ExcludeIncludeEnumFilter(excluded, included);

        return new DetectorEvaluationOptions(false, false, (rule -> detectorFilter.shouldInclude(rule.getDetectorType())));

    }

    private DetectorEvaluationTree createEvaluationTree(Extraction extraction, DetectableResult extractionResult, File directory, DetectorRule<GoModCliDetectable> rule, DetectorRuleSet detectorRuleSet) {
        DetectorEvaluation detectorEvaluation = new DetectorEvaluation(rule);

        DetectorResult extractableResult = new DetectorResult(extractionResult.getPassed(), extractionResult.toDescription(), extractionResult.getClass(), Collections.emptyList(), Collections.emptyList());
        detectorEvaluation.setExtractable(extractableResult);
        detectorEvaluation.setExtraction(extraction);
        detectorEvaluation.setApplicable(new DetectorResult(true, "", Collections.emptyList(), Collections.emptyList()));
        detectorEvaluation.setSearchable(new DetectorResult(true, "", Collections.emptyList(), Collections.emptyList()));
        detectorEvaluation.setDetectableEnvironment(new DetectableEnvironment(new File("")));
        return new DetectorEvaluationTree(directory, 0, detectorRuleSet, Collections.singletonList(detectorEvaluation), new HashSet<>());
    }
}
