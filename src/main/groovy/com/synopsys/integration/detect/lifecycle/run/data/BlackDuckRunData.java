package com.synopsys.integration.detect.lifecycle.run.data;

import com.synopsys.integration.detect.workflow.BlackDuckConnectivityManager;

public class BlackDuckRunData {
    private BlackDuckConnectivityManager connectivityManager;

    public BlackDuckRunData(final BlackDuckConnectivityManager connectivityManager) {
        this.connectivityManager = connectivityManager;
    }

    public BlackDuckConnectivityManager getConnectivityManager() {
        return connectivityManager;
    }
}
