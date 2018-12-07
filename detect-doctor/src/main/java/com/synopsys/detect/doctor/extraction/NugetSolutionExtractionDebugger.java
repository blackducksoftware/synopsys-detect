package com.synopsys.detect.doctor.extraction;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.detector.DetectorType;
import com.blackducksoftware.integration.hub.detect.detector.ExtractionId;
import com.blackducksoftware.integration.hub.detect.detector.nuget.NugetInspectorExtractor;
import com.blackducksoftware.integration.hub.detect.detector.nuget.NugetInspectorPackager;
import com.blackducksoftware.integration.hub.detect.detector.nuget.inspector.NugetInspector;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.google.gson.Gson;
import com.synopsys.detect.doctor.diagnosticparser.DetectRunInfo;
import com.synopsys.detect.doctor.logparser.LoggedDetectExtraction;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class NugetSolutionExtractionDebugger {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void debug(LoggedDetectExtraction extraction, DetectRunInfo detectRunInfo, DetectConfiguration detectConfiguration) {
        String id = extraction.extractionIdentifier;
        try {

            NugetInspectorPackager packager = new NugetInspectorPackager(new Gson(), new ExternalIdFactory());
            DetectFileFinder detectFileFinder = new DetectFileFinder();

            File extractionFolder = new File(detectRunInfo.getExtractionsFolder(), extraction.extractionIdentifier);
            List<File> extractionFiles = Arrays.asList(extractionFolder.listFiles());

            DetectFileFinder mock = Mockito.mock(DetectFileFinder.class);
            Mockito.when(mock.findFiles(Mockito.any(), Mockito.any())).thenReturn(extractionFiles);

            NugetInspectorExtractor nugetInspectorExtractor = new NugetInspectorExtractor(packager, mock, detectConfiguration);

            NugetInspector inspector = Mockito.mock(NugetInspector.class);
            Mockito.when(inspector.execute(Mockito.any(), Mockito.any())).thenReturn(new ExecutableOutput("", ""));
            File mockTarget = Mockito.mock(File.class);
            Mockito.when(mockTarget.toString()).thenReturn("mock/target");

            File mockOutput = Mockito.mock(File.class);
            Mockito.when(mockOutput.getCanonicalPath()).thenReturn("mock/output");
            Mockito.when(mockOutput.toString()).thenReturn("mock/output");

            Extraction newExtraction = nugetInspectorExtractor.extract(mockTarget, mockOutput, inspector, new ExtractionId(DetectorType.NUGET, id));
            logger.info("We did it: " + newExtraction.result.toString());
        } catch (Exception e) {
            logger.info("We did not do it: " + e.toString());
            throw new RuntimeException(e);
        }

    }
}
