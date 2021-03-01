/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.boot.decision;

public class ProductDecision {
    private final BlackDuckDecision blackDuckDecision;
    private final PolarisDecision polarisDecision;

    public ProductDecision(final BlackDuckDecision blackDuckDecision, final PolarisDecision polarisDecision) {
        this.blackDuckDecision = blackDuckDecision;
        this.polarisDecision = polarisDecision;
    }

    public BlackDuckDecision getBlackDuckDecision() {
        return blackDuckDecision;
    }

    public PolarisDecision getPolarisDecision() {
        return polarisDecision;
    }

    public boolean willRunAny() {
        return blackDuckDecision.shouldRun() || polarisDecision.shouldRun();
    }
}
