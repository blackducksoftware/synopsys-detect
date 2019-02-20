package com.synopsys.integration.detect.lifecycle.boot.decision;

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;

public class BlackDuckDecision {
    private boolean shouldRun;
    private boolean successfullyConnected;
    private boolean isOffline;

    private BlackDuckServicesFactory blackDuckServicesFactory;
    private BlackDuckServerConfig blackDuckServerConfig;

    public static BlackDuckDecision forSkipBlackduck() {
        return new BlackDuckDecision(false, false, true, null, null);
    }

    public static BlackDuckDecision forOffline() {
        return new BlackDuckDecision(true, false, true, null, null);
    }

    public static BlackDuckDecision forOnlineConnected(final BlackDuckServicesFactory blackDuckServicesFactory,
        final BlackDuckServerConfig blackDuckServerConfig) {
        return new BlackDuckDecision(true, true, false, blackDuckServicesFactory, blackDuckServerConfig);
    }

    public static BlackDuckDecision forOnlineNotConnected() {
        return new BlackDuckDecision(true, false, false, null, null);
    }

    public BlackDuckDecision(final boolean shouldRun, final boolean successfullyConnected, final boolean isOffline, final BlackDuckServicesFactory blackDuckServicesFactory,
        final BlackDuckServerConfig blackDuckServerConfig) {
        this.shouldRun = shouldRun;
        this.successfullyConnected = successfullyConnected;
        this.isOffline = isOffline;
        this.blackDuckServicesFactory = blackDuckServicesFactory;
        this.blackDuckServerConfig = blackDuckServerConfig;
    }

    public BlackDuckServicesFactory getBlackDuckServicesFactory() {
        return blackDuckServicesFactory;
    }

    public BlackDuckServerConfig getBlackDuckServerConfig() {
        return blackDuckServerConfig;
    }

    public boolean isSuccessfullyConnected() {
        return successfullyConnected;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public boolean shouldRun() {
        return shouldRun;
    }
}
