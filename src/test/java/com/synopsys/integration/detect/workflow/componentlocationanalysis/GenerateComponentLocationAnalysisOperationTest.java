package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.DetectRunId;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.file.DirectoryOptions;
import com.synopsys.integration.util.NameVersion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GenerateComponentLocationAnalysisOperationTest {

    @Test
    void testGeneratedOutputFilePathAndName(@TempDir Path tempPath) throws IOException, DetectUserFriendlyException {
        File tempDir = tempPath.toFile();
        File scanDir = new File(tempDir, "scan");
        DirectoryOptions directoryOptions = new DirectoryOptions(null, null, null, scanDir.toPath(), null, null, null);
        DetectRunId detectRunId = new DetectRunId("testId");
        DirectoryManager directoryManager = new DirectoryManager(directoryOptions, detectRunId);
        List<DeveloperScansScanView> results = new LinkedList<>();
        DeveloperScansScanView resultView = Mockito.mock(DeveloperScansScanView.class);
        results.add(resultView);
        NameVersion projectNameVersion = new NameVersion("testName", "testVersion");

        File generatedFile = (new GenerateComponentLocationAnalysisOperation()).locateComponentsForNonPersistentOnlineDetectorScan(results, directoryManager.getScanOutputDirectory(), directoryManager.getSourceDirectory());;

        String expectedFilename = String.format("components-with-locations.json", projectNameVersion.getName(), projectNameVersion.getVersion());
        String expectedPath = new File(scanDir, expectedFilename).getAbsolutePath();
        assertEquals(expectedPath, generatedFile.getAbsolutePath());
    }

}
