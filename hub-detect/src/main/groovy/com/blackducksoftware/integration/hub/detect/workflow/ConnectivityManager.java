package com.blackducksoftware.integration.hub.detect.workflow;

import java.util.Optional;

import org.springframework.util.Assert;

import com.blackducksoftware.integration.hub.detect.hub.HubServiceManager;

public class ConnectivityManager {

    private final boolean isDetectOnline;
    private final HubServiceManager hubServiceManager;

    private ConnectivityManager(boolean isDetectOnline, final HubServiceManager hubServiceManager) {
        this.isDetectOnline = isDetectOnline;
        this.hubServiceManager = hubServiceManager;
    }

    public static ConnectivityManager offline() {
        return new ConnectivityManager(false, null);
    }

    public static ConnectivityManager online(HubServiceManager hubServiceManager) {
        Assert.notNull(hubServiceManager, "Online detect needs a valid hub services manager.");
        return new ConnectivityManager(true, hubServiceManager);
    }

    public boolean isDetectOnline() {
        return isDetectOnline;
    }

    public Optional<HubServiceManager> getHubServiceManager() {
        return Optional.ofNullable(hubServiceManager);
    }
}
