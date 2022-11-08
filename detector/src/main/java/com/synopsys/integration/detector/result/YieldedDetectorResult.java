package com.synopsys.integration.detector.result;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class YieldedDetectorResult extends FailedDetectorResult {
    public YieldedDetectorResult(Set<String> yieldedTo) {
        super(String.format("Yielded to detectors: %s", StringUtils.join(yieldedTo, ",")));
    }
}
