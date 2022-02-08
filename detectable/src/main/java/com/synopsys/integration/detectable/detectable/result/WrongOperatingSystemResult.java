package com.synopsys.integration.detectable.detectable.result;

import com.synopsys.integration.util.OperatingSystemType;

public class WrongOperatingSystemResult extends FailedDetectableResult {
    private final OperatingSystemType currentOperatingSystem;

    public WrongOperatingSystemResult(OperatingSystemType currentOperatingSystem) {
        this.currentOperatingSystem = currentOperatingSystem;
    }

    @Override
    public String toDescription() {
        return String.format("Cannot run on %s", currentOperatingSystem.toString());
    }

}
