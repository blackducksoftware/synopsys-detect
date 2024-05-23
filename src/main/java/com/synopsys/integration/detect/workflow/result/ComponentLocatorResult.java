package com.synopsys.integration.detect.workflow.result;

import java.util.Collections;
import java.util.List;

public class ComponentLocatorResult implements DetectResult {
    private final String resultFilePath;

    public ComponentLocatorResult(String resultFilePath) {
        this.resultFilePath = resultFilePath;
    }

    @Override
    public String getResultLocation() {
        return resultFilePath;
    }

    @Override
    public String getResultMessage() {
        return String.format("Component Location Analysis File: %s", resultFilePath);
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
