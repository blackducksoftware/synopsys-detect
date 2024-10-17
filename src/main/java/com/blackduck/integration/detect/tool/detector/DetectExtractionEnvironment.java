package com.blackduck.integration.detect.tool.detector;

import java.io.File;

import com.blackduck.integration.detect.tool.detector.extraction.ExtractionId;
import com.blackduck.integration.detectable.extraction.ExtractionEnvironment;

public class DetectExtractionEnvironment extends ExtractionEnvironment {
    private final ExtractionId extractionId;

    public DetectExtractionEnvironment(File outputDirectory, ExtractionId extractionId) {
        super(outputDirectory);
        this.extractionId = extractionId;
    }

    public ExtractionId getExtractionId() {
        return extractionId;
    }
}
