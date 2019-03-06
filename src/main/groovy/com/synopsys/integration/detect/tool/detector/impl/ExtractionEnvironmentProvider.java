package com.synopsys.integration.detect.tool.detector.impl;

import java.io.File;

import com.synopsys.integration.detect.tool.detector.ExtractionId;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detector.base.DetectorEvaluation;

public class ExtractionEnvironmentProvider {
    private final DirectoryManager directoryManager;
    private int count = 0;

    public ExtractionEnvironmentProvider(DirectoryManager directoryManager){
        this.directoryManager = directoryManager;
    }

    public ExtractionEnvironment createExtractionEnvironment(DetectorEvaluation detectorEvaluation){
        ExtractionId extractionId = new ExtractionId(detectorEvaluation.getDetectorRule().getDetectorType(), count);
        count = count + 1;

        File outputDirectory = directoryManager.getExtractionOutputDirectory(extractionId);
        return new DetectExtractionEnvironment(outputDirectory, extractionId);
    }

    public ExtractionEnvironment createExtractionEnvironment(String name){
        ExtractionId extractionId = new ExtractionId(name, count);
        count = count + 1;

        File outputDirectory = directoryManager.getExtractionOutputDirectory(extractionId);
        return new DetectExtractionEnvironment(outputDirectory, extractionId);
    }
}
