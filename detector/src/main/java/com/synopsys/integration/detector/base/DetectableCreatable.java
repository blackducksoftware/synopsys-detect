package com.synopsys.integration.detector.base;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;

@FunctionalInterface
public interface DetectableCreatable {
    Detectable createDetectable(DetectableEnvironment environment);
}
