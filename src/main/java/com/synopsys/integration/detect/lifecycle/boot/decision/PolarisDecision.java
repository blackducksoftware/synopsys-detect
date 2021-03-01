/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.boot.decision;

import com.synopsys.integration.polaris.common.configuration.PolarisServerConfig;

public class PolarisDecision {
    private final boolean shouldRun;
    private final PolarisServerConfig polarisServerConfig;

    public static PolarisDecision skip() {
        return new PolarisDecision(false, null);
    }

    public static PolarisDecision runOnline(final PolarisServerConfig polarisServerConfig) {
        return new PolarisDecision(true, polarisServerConfig);
    }

    public PolarisDecision(final boolean shouldRun, final PolarisServerConfig polarisServerConfig) {
        this.shouldRun = shouldRun;
        this.polarisServerConfig = polarisServerConfig;
    }

    public PolarisServerConfig getPolarisServerConfig() {
        return polarisServerConfig;
    }

    public boolean shouldRun() {
        return shouldRun;
    }
}
