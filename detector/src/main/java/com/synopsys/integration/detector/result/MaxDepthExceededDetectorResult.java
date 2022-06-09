package com.synopsys.integration.detector.result;

public class MaxDepthExceededDetectorResult extends FailedDetectorResult {
    public MaxDepthExceededDetectorResult(int depth, int maxDepth) {
        super(String.format("Max depth of %d exceeded by %d", maxDepth, depth));
    }
}
