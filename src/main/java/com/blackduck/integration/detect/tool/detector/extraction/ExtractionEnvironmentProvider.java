package com.blackduck.integration.detect.tool.detector.extraction;

import java.io.File;

import com.blackduck.integration.detect.tool.detector.DetectExtractionEnvironment;
import com.blackduck.integration.detect.workflow.file.DirectoryManager;
import com.blackduck.integration.detectable.extraction.ExtractionEnvironment;
import com.blackduck.integration.detector.base.DetectorType;

public class ExtractionEnvironmentProvider {
    private final DirectoryManager directoryManager;
    private int count = 0;

    public ExtractionEnvironmentProvider(DirectoryManager directoryManager) {
        this.directoryManager = directoryManager;
    }

    public ExtractionEnvironment createExtractionEnvironment(DetectorType detectorType) {
        ExtractionId extractionId = new ExtractionId(detectorType, count);
        count = count + 1;

        File outputDirectory = directoryManager.getExtractionOutputDirectory(extractionId);
        return new DetectExtractionEnvironment(outputDirectory, extractionId);
    }

    public ExtractionEnvironment createExtractionEnvironment(String name) {
        ExtractionId extractionId = new ExtractionId(name, count);
        count = count + 1;

        File outputDirectory = directoryManager.getExtractionOutputDirectory(extractionId);
        return new DetectExtractionEnvironment(outputDirectory, extractionId);
    }
}
