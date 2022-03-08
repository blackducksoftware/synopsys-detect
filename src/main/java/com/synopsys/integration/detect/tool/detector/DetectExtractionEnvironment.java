package com.synopsys.integration.detect.tool.detector;

import java.io.File;

import com.synopsys.integration.detect.tool.detector.extraction.ExtractionId;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

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
