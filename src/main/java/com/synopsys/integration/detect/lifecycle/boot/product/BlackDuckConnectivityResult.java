package com.synopsys.integration.detect.lifecycle.boot.product;

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;

public class BlackDuckConnectivityResult {
    private boolean successfullyConnected;

    //if failure, the following is populated
    private String failureReason;

    //if success, the following is populated
    private BlackDuckServicesFactory blackDuckServicesFactory;
    private BlackDuckServerConfig blackDuckServerConfig;

    private BlackDuckConnectivityResult(boolean successfullyConnected, String failureReason,
        BlackDuckServicesFactory blackDuckServicesFactory, BlackDuckServerConfig blackDuckServerConfig) {
        this.successfullyConnected = successfullyConnected;
        this.failureReason = failureReason;
        this.blackDuckServicesFactory = blackDuckServicesFactory;
        this.blackDuckServerConfig = blackDuckServerConfig;
    }

    public static BlackDuckConnectivityResult success(BlackDuckServicesFactory blackDuckServicesFactory, BlackDuckServerConfig blackDuckServerConfig) {
        return new BlackDuckConnectivityResult(true, null, blackDuckServicesFactory, blackDuckServerConfig);
    }

    public static BlackDuckConnectivityResult failure(String reason) {
        return new BlackDuckConnectivityResult(false, reason, null, null);
    }

    public boolean isSuccessfullyConnected() {
        return successfullyConnected;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public BlackDuckServicesFactory getBlackDuckServicesFactory() {
        return blackDuckServicesFactory;
    }

    public BlackDuckServerConfig getBlackDuckServerConfig() {
        return blackDuckServerConfig;
    }
}
