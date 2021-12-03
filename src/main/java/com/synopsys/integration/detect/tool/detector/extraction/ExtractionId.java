package com.synopsys.integration.detect.tool.detector.extraction;

import com.synopsys.integration.detector.base.DetectorType;

public class ExtractionId {
    private final Integer id;
    private final String extractionType;

    public ExtractionId(DetectorType detectorType, Integer id) {
        extractionType = detectorType.toString();
        this.id = id;
    }

    public ExtractionId(String extractionType, Integer id) {
        this.id = id;
        this.extractionType = extractionType;
    }

    public String toUniqueString() {
        return extractionType + "-" + id;
    }

    public Integer getId() {
        return id;
    }
}
