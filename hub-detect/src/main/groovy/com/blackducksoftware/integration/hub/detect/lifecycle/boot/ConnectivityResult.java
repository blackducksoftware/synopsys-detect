package com.blackducksoftware.integration.hub.detect.lifecycle.boot;

import com.blackducksoftware.integration.hub.detect.workflow.phonehome.PhoneHomeManager;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;

public class ConnectivityResult {
    private boolean successfullyConnected;

    //if failure, the following is populated
    private String failureReason;

    //if success, the following is populated
    private BlackDuckServicesFactory blackDuckServicesFactory;
    private PhoneHomeManager phoneHomeManager;
    private BlackDuckServerConfig blackDuckServerConfig;

    private ConnectivityResult(final boolean successfullyConnected, final String failureReason,
        final BlackDuckServicesFactory blackDuckServicesFactory, final PhoneHomeManager phoneHomeManager, final BlackDuckServerConfig blackDuckServerConfig) {
        this.successfullyConnected = successfullyConnected;
        this.failureReason = failureReason;
        this.blackDuckServicesFactory = blackDuckServicesFactory;
        this.phoneHomeManager = phoneHomeManager;
        this.blackDuckServerConfig = blackDuckServerConfig;
    }

    public static ConnectivityResult success(final BlackDuckServicesFactory blackDuckServicesFactory, final PhoneHomeManager phoneHomeManager, final BlackDuckServerConfig blackDuckServerConfig) {
        return new ConnectivityResult(true, null, blackDuckServicesFactory, phoneHomeManager, blackDuckServerConfig);
    }

    public static ConnectivityResult failure(String reason) {
        return new ConnectivityResult(true, reason, null, null, null);
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

    public PhoneHomeManager getPhoneHomeManager() {
        return phoneHomeManager;
    }

    public BlackDuckServerConfig getBlackDuckServerConfig() {
        return blackDuckServerConfig;
    }
}
