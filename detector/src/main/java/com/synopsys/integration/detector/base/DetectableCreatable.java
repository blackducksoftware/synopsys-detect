package com.synopsys.integration.detector.base;

import com.blackduck.integration.detectable.Detectable;
import com.blackduck.integration.detectable.DetectableEnvironment;

@FunctionalInterface
public interface DetectableCreatable {
    Detectable createDetectable(DetectableEnvironment environment);
}
