package com.synopsys.integration.detect.workflow.result;

import java.util.List;

public interface DetectResult {
    String getResultLocation();

    String getResultMessage();

    List<String> getResultSubMessages();

    List<String> getTransitiveUpgradeGuidanceSubMessages();
}
