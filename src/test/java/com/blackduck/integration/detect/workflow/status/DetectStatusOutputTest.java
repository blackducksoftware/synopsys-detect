package com.blackduck.integration.detect.workflow.status;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import com.blackduck.integration.detect.workflow.DetectRunId;
import com.blackduck.integration.detect.workflow.file.DirectoryManager;
import com.blackduck.integration.detect.workflow.file.DirectoryOptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class DetectStatusOutputTest {
    @Test
    void testJsonStatusOutputDirectory(@TempDir Path tempPath) throws IOException {
        File tempDir = tempPath.toFile();
        File scanDir = new File(tempDir, "scan");
        DirectoryOptions directoryOptions = new DirectoryOptions(null, null, null, scanDir.toPath(), null, null, tempPath);
        DetectRunId detectRunId = new DetectRunId("testId", DetectRunId.generateIntegratedMatchingCorrelationId());
        DirectoryManager directoryManager = new DirectoryManager(directoryOptions, detectRunId);

        assertEquals(tempPath.toFile(), directoryManager.getJsonStatusOutputDirectory());
    }
}
