package com.synopsys.detect.doctor.extraction;

import java.io.File;

import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.detector.gradle.GradleInspectorExtractor;
import com.blackducksoftware.integration.hub.detect.detector.gradle.GradleReportParser;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.synopsys.detect.doctor.diagnosticparser.DetectRunInfo;
import com.synopsys.detect.doctor.logparser.LoggedDetectExtraction;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class GradleExtractionDebugger {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void debug(LoggedDetectExtraction extraction, DetectRunInfo detectRunInfo, DetectConfiguration detectConfiguration) throws ExecutableRunnerException {
        String id = extraction.extractionIdentifier;
        ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);

        Mockito.when(executableRunner.execute(Mockito.any())).thenReturn(new ExecutableOutput("", ""));

        DetectFileFinder detectFileFinder = new DetectFileFinder();

        File mockSourceFile = Mockito.mock(File.class);

        File outputDirectory = new File(detectRunInfo.getExtractionsFolder(), extraction.extractionIdentifier);

        GradleInspectorExtractor gradleInspectorExtractor = new GradleInspectorExtractor(executableRunner, detectFileFinder, new GradleReportParser(new ExternalIdFactory()), detectConfiguration);

        Extraction extractionResult = gradleInspectorExtractor.extract(mockSourceFile, "", "", outputDirectory);

    }
}