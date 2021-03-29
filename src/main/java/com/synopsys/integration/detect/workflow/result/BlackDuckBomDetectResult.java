/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.result;

import java.util.Collections;
import java.util.List;

public class BlackDuckBomDetectResult implements DetectResult {
    private final String projectComponentsLink;

    public BlackDuckBomDetectResult(String projectComponentsLink) {
        this.projectComponentsLink = projectComponentsLink;
    }

    @Override
    public String getResultLocation() {
        return projectComponentsLink;
    }

    @Override
    public String getResultMessage() {
        return String.format("Black Duck Project BOM: %s", projectComponentsLink);
    }

    @Override
    public List<String> getResultSubMessages() {
        return Collections.emptyList();
    }
}
