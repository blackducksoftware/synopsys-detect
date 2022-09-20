package com.synopsys.integration.detect.lifecycle.boot.product;

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;

public class BlackDuckConnectivityResult {
    private final boolean successfullyConnected;
    private String contactedServerVersion = "no connected server version";

    //if failure, the following is populated
    private final String failureReason;

    //if success, the following is populated
    private final BlackDuckServicesFactory blackDuckServicesFactory;
    private final BlackDuckServerConfig blackDuckServerConfig;

    private BlackDuckConnectivityResult(
        boolean successfullyConnected,
        String failureReason,
        BlackDuckServicesFactory blackDuckServicesFactory,
        BlackDuckServerConfig blackDuckServerConfig
    ) {
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

    public void setContactedServerVersion(String version) {
        if (null == version && version.equals("")) {
            return;
        }
        this.contactedServerVersion = version;
    }

    public String getContactedServerVersion() {
        return this.contactedServerVersion;
    }
}
