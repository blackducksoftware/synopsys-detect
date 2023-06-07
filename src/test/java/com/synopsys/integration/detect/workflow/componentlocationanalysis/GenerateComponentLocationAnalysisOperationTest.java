package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.google.gson.Gson;
import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.DetectRunId;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.file.DirectoryOptions;
import com.synopsys.integration.util.NameVersion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.zeroturnaround.zip.commons.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GenerateComponentLocationAnalysisOperationTest {

    @Test
    void test(@TempDir Path tempPath) throws IOException, DetectUserFriendlyException {
        Gson gson = Mockito.mock(Gson.class);

        File tempDir = tempPath.toFile();
        File scanDir = new File(tempDir, "scan");
        DirectoryOptions directoryOptions = new DirectoryOptions(null, null, null, scanDir.toPath(), null, null, null);
        DetectRunId detectRunId = new DetectRunId("testId");
        DirectoryManager directoryManager = new DirectoryManager(directoryOptions, detectRunId);
        List<DeveloperScansScanView> results = new LinkedList<>();
        DeveloperScansScanView resultView = Mockito.mock(DeveloperScansScanView.class);
        results.add(resultView);
        NameVersion projectNameVersion = new NameVersion("testName", "testVersion");



        String mockedResultsJsonString = "some json string";
        Mockito.when(gson.toJson(results)).thenReturn(mockedResultsJsonString);

        File generatedFile = GenerateComponentLocationAnalysisOperation.forNonPersistentOnlinePkgMngrScan(results, directoryManager.getScanOutputDirectory());;

        String expectedFilename = String.format("components_with_locations.json", projectNameVersion.getName(), projectNameVersion.getVersion());
        String expectedPath = new File(scanDir, expectedFilename).getAbsolutePath();
        assertEquals(expectedPath, generatedFile.getAbsolutePath());

        String generatedString = FileUtils.readFileToString(generatedFile);
        assertEquals(mockedResultsJsonString, generatedString);
    }

}
