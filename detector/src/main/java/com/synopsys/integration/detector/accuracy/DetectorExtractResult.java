package com.synopsys.integration.detector.accuracy;

import com.synopsys.integration.detectable.extraction.Extraction;

public class DetectorExtractResult {

    public static DetectorExtractResult extracted(Extraction extraction) {
        return new DetectorExtractResult();
    }

    public static DetectorExtractResult noExtractions() {
        return new DetectorExtractResult();
    }
}
