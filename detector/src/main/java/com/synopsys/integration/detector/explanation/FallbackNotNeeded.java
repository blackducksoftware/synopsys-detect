package com.synopsys.integration.detector.explanation;

import com.synopsys.integration.detectable.detectable.explanation.Explanation;

public class FallbackNotNeeded extends Explanation {
    private final String passingDetector;

    public FallbackNotNeeded(final String passingDetector) {
        this.passingDetector = passingDetector;
    }

    @Override
    public String describeSelf() {
        return "Not needed as fallback: " + passingDetector + " successful";
    }
}
