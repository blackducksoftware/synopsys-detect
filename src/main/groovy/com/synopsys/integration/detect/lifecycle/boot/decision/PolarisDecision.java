package com.synopsys.integration.detect.lifecycle.boot.decision;

import com.synopsys.integration.polaris.common.configuration.PolarisServerConfig;

public class PolarisDecision {
    private final boolean shouldRun;
    private final PolarisServerConfig polarisServerConfig;

    public static PolarisDecision forSkipPolaris() {
        return new PolarisDecision(false, null);
    }

    public static PolarisDecision forOnline(final PolarisServerConfig polarisServerConfig) {
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
