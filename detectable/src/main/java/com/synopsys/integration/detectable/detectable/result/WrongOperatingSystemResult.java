package com.synopsys.integration.detectable.detectable.result;

import com.synopsys.integration.util.OperatingSystemType;

public class WrongOperatingSystemResult extends FailedDetectableResult {
    private static final String PREFIX = "Cannot run on ";

    public WrongOperatingSystemResult(OperatingSystemType currentOperatingSystem) {
        super(PREFIX, currentOperatingSystem.toString());
    }
}
