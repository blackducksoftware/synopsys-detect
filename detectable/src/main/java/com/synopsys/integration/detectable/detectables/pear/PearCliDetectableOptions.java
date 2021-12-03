package com.synopsys.integration.detectable.detectables.pear;

public class PearCliDetectableOptions {
    private final boolean onlyGatherRequired;

    public PearCliDetectableOptions(boolean onlyGatherRequired) {
        this.onlyGatherRequired = onlyGatherRequired;
    }

    public boolean onlyGatherRequired() {
        return onlyGatherRequired;
    }
}
