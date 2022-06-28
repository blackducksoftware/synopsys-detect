package com.synopsys.integration.detect.workflow.git;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.blackduck.bdio2.model.GitInfo;
import com.synopsys.integration.detect.tool.detector.report.DetectorDirectoryReport;
import com.synopsys.integration.detect.tool.detector.report.detectable.ExtractedDetectableReport;
import com.synopsys.integration.detect.tool.detector.report.rule.ExtractedDetectorRuleReport;
import com.synopsys.integration.detectable.detectables.git.cli.GitCliExtractor;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.rule.DetectorRule;

class DetectorGitProjectInfoDeciderTest {
    private static final int rootLevel = 0;
    private static final int nestedLevel = 2;
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

    @Test
    void testFindsMin() {
        DetectorRule gitRule = mockGitRule();
        DetectorDirectoryReport rootDirectoryReport = mockDetectorDirectoryReportForGitRule(gitRule, rootGitInfo, rootLevel);
        DetectorDirectoryReport nestedDirectoryReport = mockDetectorDirectoryReportForGitRule(gitRule, nestedGitInfo, nestedLevel);

        Optional<GitInfo> decidedGitInfo = gitProjectInfoDecider.decideSuggestion(Arrays.asList(
            rootDirectoryReport,
            nestedDirectoryReport
        ));

        assertTrue(decidedGitInfo.isPresent());
        assertEquals(rootGitInfo, decidedGitInfo.get());
    }

    @Test
    void testFindsNested() {
        DetectorDirectoryReport rootDirectoryReport = mockDetectorDirectoryReportForNpm(rootLevel);

        DetectorRule gitRule = mockGitRule();
        DetectorDirectoryReport nestedDirectoryReport = mockDetectorDirectoryReportForGitRule(gitRule, nestedGitInfo, nestedLevel);

        Optional<GitInfo> decidedGitInfo = gitProjectInfoDecider.decideSuggestion(Arrays.asList(
            rootDirectoryReport,
            nestedDirectoryReport
        ));

        assertTrue(decidedGitInfo.isPresent());
        assertEquals(nestedGitInfo, decidedGitInfo.get());
    }

    @Test
    void testNoGitInfo() {
        DetectorDirectoryReport rootDirectoryReport = mockDetectorDirectoryReportForNpm(rootLevel);
        DetectorDirectoryReport nestedDirectoryReport = mockDetectorDirectoryReportForNpm(nestedLevel);

        Optional<GitInfo> decidedGitInfo = gitProjectInfoDecider.decideSuggestion(Arrays.asList(
            rootDirectoryReport,
            nestedDirectoryReport
        ));

        assertFalse(decidedGitInfo.isPresent());
    }

    @NotNull
    private DetectorDirectoryReport mockDetectorDirectoryReportForNpm(int level) {
        ExtractedDetectorRuleReport extractedDetectorRuleReport = Mockito.mock(ExtractedDetectorRuleReport.class);
        DetectorRule npmRule = Mockito.mock(DetectorRule.class);
        Mockito.when(npmRule.getDetectorType()).thenReturn(DetectorType.NPM);
        ExtractedDetectableReport extractedDetectableReport = Mockito.mock(ExtractedDetectableReport.class);
        Extraction extractionValue = Mockito.mock(Extraction.class);
        Mockito.when(extractedDetectableReport.getExtraction()).thenReturn(extractionValue);
        Mockito.when(extractedDetectorRuleReport.getExtractedDetectable()).thenReturn(extractedDetectableReport);
        Mockito.when(extractedDetectorRuleReport.getRule()).thenReturn(npmRule);
        Mockito.when(extractedDetectorRuleReport.getDepth()).thenReturn(level);
        File dir = Mockito.mock(File.class);
        DetectorDirectoryReport rootDirectoryReport = new DetectorDirectoryReport(dir,
            level, Collections.emptyList(), Arrays.asList(extractedDetectorRuleReport), Collections.emptyList()
        );
        return rootDirectoryReport;
    }

    @NotNull
    private DetectorRule mockGitRule() {
        DetectorRule gitRule = Mockito.mock(DetectorRule.class);
        Mockito.when(gitRule.getDetectorType()).thenReturn(DetectorType.GIT);
        return gitRule;
    }

    @NotNull
    private DetectorDirectoryReport mockDetectorDirectoryReportForGitRule(DetectorRule gitRule, GitInfo gitInfo, int level) {
        ExtractedDetectableReport gitExtractedDetectableReport = Mockito.mock(ExtractedDetectableReport.class);
        Extraction gitExtractionValue = Mockito.mock(Extraction.class);
        Mockito.when(gitExtractionValue.hasMetadata(Mockito.any())).thenReturn(true);
        Mockito.when(gitExtractionValue.getMetaData(GitCliExtractor.EXTRACTION_METADATA_KEY)).thenReturn(Optional.of(gitInfo));
        Mockito.when(gitExtractedDetectableReport.getExtraction()).thenReturn(gitExtractionValue);
        ExtractedDetectorRuleReport extractedDetectorRuleReport = Mockito.mock(ExtractedDetectorRuleReport.class);
        Mockito.when(extractedDetectorRuleReport.getExtractedDetectable()).thenReturn(gitExtractedDetectableReport);
        Mockito.when(extractedDetectorRuleReport.getRule()).thenReturn(gitRule);
        Mockito.when(extractedDetectorRuleReport.getDepth()).thenReturn(level);
        File nestedDir = Mockito.mock(File.class);
        DetectorDirectoryReport directoryReport = new DetectorDirectoryReport(nestedDir,
            level, Collections.emptyList(), Arrays.asList(extractedDetectorRuleReport), Collections.emptyList()
        );
        return directoryReport;
    }
}