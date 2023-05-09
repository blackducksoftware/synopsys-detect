package com.synopsys.integration.detect;

import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.log.LogLevel;
import com.synopsys.integration.log.SilentIntLogger;
import com.synopsys.integration.rest.client.IntHttpClient;
import com.synopsys.integration.rest.proxy.ProxyInfo;

public class ApplicationUpdaterUtility {
    
    protected IntHttpClient getIntHttpClient(boolean trustCertificate, ProxyInfo proxyInfo) {
        final SilentIntLogger silentLogger = new SilentIntLogger();
        silentLogger.setLogLevel(LogLevel.WARN);
        return new IntHttpClient(silentLogger,
                BlackDuckServicesFactory.createDefaultGsonBuilder().setPrettyPrinting().create(),
                IntHttpClient.DEFAULT_TIMEOUT, 
                trustCertificate, 
                proxyInfo
        );
    }
}