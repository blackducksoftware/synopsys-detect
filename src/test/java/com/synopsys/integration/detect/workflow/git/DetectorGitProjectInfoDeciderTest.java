package com.synopsys.integration.detect.workflow.git;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.blackduck.bdio2.model.GitInfo;
import com.synopsys.integration.detect.tool.detector.report.DetectorDirectoryReport;
import com.synopsys.integration.detect.tool.detector.report.detectable.ExtractedDetectableReport;
import com.synopsys.integration.detect.tool.detector.report.rule.EvaluatedDetectorRuleReport;
import com.synopsys.integration.detect.tool.detector.report.rule.ExtractedDetectorRuleReport;
import com.synopsys.integration.detect.tool.detector.report.rule.NotFoundDetectorRuleReport;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectables.git.cli.GitCliExtractor;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detector.base.DetectableCreatable;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.rule.DetectableDefinition;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.EntryPoint;
import com.synopsys.integration.detector.rule.SearchRule;

class DetectorGitProjectInfoDeciderTest {
    private final DetectorGitProjectInfoDecider gitProjectInfoDecider = new DetectorGitProjectInfoDecider();

    private final GitInfo rootGitInfo = new GitInfo(
        "https://github.com/blackducksoftware/synopsys-detect",
        "95ef3bb9ead52be4bb2c766fafe4b8a4fa1c3d3c",
        "master"
    );

    private final GitInfo nestedGitInfo = new GitInfo(
        "https://github.com/blackducksoftware/blackduck-alert.git",
        "582ce522f65847c0723ccb43c1dfc4a922bf25cf",
        "master"
    );

    // TODO
    @Test
    void testFindsMin() {
        ExtractedDetectorRuleReport rootExtractedDetectorRuleReport = Mockito.mock(ExtractedDetectorRuleReport.class, Mockito.RETURNS_DEEP_STUBS);
        DetectorRule gitRule = Mockito.mock(DetectorRule.class);
        Mockito.when(gitRule.getDetectorType()).thenReturn(DetectorType.GIT);
        // r.getExtractedDetectable().getExtraction().hasMetadata(GitCliExtractor.EXTRACTION_METADATA_KEY))
        ExtractedDetectableReport rootGitExtractedDetectableReport = Mockito.mock(ExtractedDetectableReport.class);
        Extraction rootGitExtractionValue = Mockito.mock(Extraction.class);
        Mockito.when(rootGitExtractionValue.hasMetadata(Mockito.any())).thenReturn(true);
        // .getMetaData(GitCliExtractor.EXTRACTION_METADATA_KEY));
        GitInfo rootGitInfo = Mockito.mock(GitInfo.class);
        Mockito.when(rootGitExtractionValue.getMetaData(GitCliExtractor.EXTRACTION_METADATA_KEY)).thenReturn(Optional.of(rootGitInfo));
        Mockito.when(rootGitExtractedDetectableReport.getExtraction()).thenReturn(rootGitExtractionValue);
        Mockito.when(rootExtractedDetectorRuleReport.getExtractedDetectable()).thenReturn(rootGitExtractedDetectableReport);
        Mockito.when(rootExtractedDetectorRuleReport.getRule()).thenReturn(gitRule);
        Mockito.when(rootExtractedDetectorRuleReport.getDepth()).thenReturn(0);
        File rootDir = Mockito.mock(File.class);

        ExtractedDetectableReport nestedGitExtractedDetectableReport = Mockito.mock(ExtractedDetectableReport.class);
        Extraction nestedGitExtractionValue = Mockito.mock(Extraction.class);
        Mockito.when(nestedGitExtractionValue.hasMetadata(Mockito.any())).thenReturn(true);
        // .getMetaData(GitCliExtractor.EXTRACTION_METADATA_KEY));
        GitInfo nestedGitInfo = Mockito.mock(GitInfo.class);
        Mockito.when(nestedGitExtractionValue.getMetaData(GitCliExtractor.EXTRACTION_METADATA_KEY)).thenReturn(Optional.of(nestedGitInfo));
        Mockito.when(nestedGitExtractedDetectableReport.getExtraction()).thenReturn(nestedGitExtractionValue);
//        Mockito.when(nestedExtractedDetectorRuleReport.getExtractedDetectable()).thenReturn(nestedGitExtractedDetectableReport);
//        Mockito.when(nestedExtractedDetectorRuleReport.getRule()).thenReturn(gitRule);
//        Mockito.when(nestedExtractedDetectorRuleReport.getDepth()).thenReturn(2);
        //File nestedDir = Mockito.mock(File.class);

//        File directory,
//        int depth,
//        List<NotFoundDetectorRuleReport> notFoundDetectors,
//        List<ExtractedDetectorRuleReport> extractedDetectors,
//        List<EvaluatedDetectorRuleReport> notExtractedDetectors

        DetectorDirectoryReport rootDirectoryReport = new DetectorDirectoryReport(rootDir,
            0, Collections.emptyList(), Arrays.asList(rootExtractedDetectorRuleReport), Collections.emptyList());

        ExtractedDetectorRuleReport nestedExtractedDetectorRuleReport = Mockito.mock(ExtractedDetectorRuleReport.class, Mockito.RETURNS_DEEP_STUBS);
        File nestedDir = Mockito.mock(File.class);
        DetectorDirectoryReport nestedDirectoryReport = new DetectorDirectoryReport(rootDir,
            2, Collections.emptyList(), Arrays.asList(nestedExtractedDetectorRuleReport), Collections.emptyList());

        Mockito.when(nestedExtractedDetectorRuleReport.getExtractedDetectable()).thenReturn(nestedGitExtractedDetectableReport);
        Mockito.when(nestedExtractedDetectorRuleReport.getRule()).thenReturn(gitRule);
        Mockito.when(nestedExtractedDetectorRuleReport.getDepth()).thenReturn(2);

//        List<DetectorDirectoryReport> detectorDirectoryReports;
//        DetectorEvaluation rootEvaluation = createEvaluation(DetectorType.GIT, 0);
//        Extraction extractionWithMetadata = createExtractionWithMetadata(rootGitInfo);
//        Mockito.when(rootEvaluation.getExtraction()).thenReturn(extractionWithMetadata);
//
//        DetectorEvaluation nestedEvaluation = createEvaluation(DetectorType.GIT, 1);
//        Extraction nestExtractionWithMetadata = createExtractionWithMetadata(nestedGitInfo);
//        Mockito.when(nestedEvaluation.getExtraction()).thenReturn(nestExtractionWithMetadata);

        Optional<GitInfo> decidedGitInfo = gitProjectInfoDecider.decideSuggestion(Arrays.asList(
            rootDirectoryReport,
            nestedDirectoryReport
        ));

        assertTrue(decidedGitInfo.isPresent());
        assertEquals(rootGitInfo, decidedGitInfo.get());
//        assertEquals(rootGitInfo.getSourceRepository(), decidedGitInfo.get().getSourceRepository());
//        assertEquals(rootGitInfo.getSourceRevision(), decidedGitInfo.get().getSourceRevision());
//        assertEquals(rootGitInfo.getSourceBranch(), decidedGitInfo.get().getSourceBranch());
    }

    /* TODO
    @Test
    void findsNested() {
        // GIT isn't found at root
        DetectorEvaluation rootEvaluation = createEvaluation(DetectorType.NPM, 0);
        Extraction extractionWithMetadata = createExtractionWithMetadata(rootGitInfo);
        Mockito.when(rootEvaluation.getExtraction()).thenReturn(extractionWithMetadata);

        Extraction nestedExtractionWithMetadata = createExtractionWithMetadata(nestedGitInfo);
        DetectorEvaluation nestedEvaluation = createEvaluation(DetectorType.GIT, 1);
        Mockito.when(nestedEvaluation.getExtraction()).thenReturn(nestedExtractionWithMetadata);

        Optional<GitInfo> decidedGitInfo = gitProjectInfoDecider.decideSuggestion(Arrays.asList(
            rootEvaluation,
            nestedEvaluation
        ));

        assertTrue(decidedGitInfo.isPresent());
        assertEquals(nestedGitInfo.getSourceRepository(), decidedGitInfo.get().getSourceRepository());
        assertEquals(nestedGitInfo.getSourceRevision(), decidedGitInfo.get().getSourceRevision());
        assertEquals(nestedGitInfo.getSourceBranch(), decidedGitInfo.get().getSourceBranch());
    }

    @Test
    void noGitInfo() {
        // All evaluations are not GIT
        DetectorEvaluation rootEvaluation = createEvaluation(DetectorType.NPM, 0);
        rootEvaluation.setExtraction(createExtractionWithMetadata(rootGitInfo));

        DetectorEvaluation nestedEvaluation = createEvaluation(DetectorType.NPM, 1);
        nestedEvaluation.setExtraction(createExtractionWithMetadata(nestedGitInfo));

        Optional<GitInfo> decidedGitInfo = gitProjectInfoDecider.decideSuggestion(Arrays.asList(
            rootEvaluation,
            nestedEvaluation
        ));

        assertFalse(decidedGitInfo.isPresent());
    }

    private Extraction createExtractionWithMetadata(GitInfo gitInfo) {
        Extraction extraction = Mockito.mock(Extraction.class);
        Mockito.when(extraction.hasMetadata(GitCliExtractor.EXTRACTION_METADATA_KEY)).thenReturn(true);
        Mockito.when(extraction.getMetaData(GitCliExtractor.EXTRACTION_METADATA_KEY)).thenReturn(Optional.of(gitInfo));
        return extraction;
    }


    private DetectorEvaluation createEvaluation(DetectorType detectorType, int depth) {
        // https://www.javadoc.io/doc/org.mockito/mockito-core/2.2.9/org/mockito/Mockito.html#RETURNS_DEEP_STUBS
        // The doc seems to be very against multi-layer mocking, so that's something to be considered when refactoring DetectorEvaluation/Extraction classes.
        DetectorEvaluation evaluation = Mockito.mock(DetectorEvaluation.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(evaluation.getDetectorType()).thenReturn(detectorType);
        Mockito.when(evaluation.wasExtractionSuccessful()).thenReturn(true);

        SearchEnvironment searchEnvironment = Mockito.mock(SearchEnvironment.class);
        Mockito.when(searchEnvironment.getDepth()).thenReturn(depth);
        evaluation.setSearchEnvironment(searchEnvironment);

        return evaluation;
    }
     */
}