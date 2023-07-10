package com.synopsys.integration.detect.workflow.result;

import java.util.Collections;
import java.util.List;

public class AirGapDetectResult implements DetectResult {
    private final String airGapFolder;

    public AirGapDetectResult(String airGapFolder) {
        this.airGapFolder = airGapFolder;
    }

    @Override
    public String getResultLocation() {
        return airGapFolder;
    }

    @Override
    public String getResultMessage() {
        return String.format("Detect Air Gap Zip: %s", airGapFolder);
    }

    @Override
    public List<String> getResultSubMessages() {
        return Collections.emptyList();
    }

    @Override
    public List<String> getTransitiveUpgradeGuidanceSubMessages() {
        return Collections.emptyList();
    }
}
