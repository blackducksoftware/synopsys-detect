package com.synopsys.integration.detect.configuration.connection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.common.util.ProxyUtil;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.SilentIntLogger;
import com.synopsys.integration.rest.client.IntHttpClient;
import com.synopsys.integration.rest.proxy.ProxyInfo;

public class ConnectionFactory {
    private final ConnectionDetails connectionDetails;

    public ConnectionFactory(ConnectionDetails connectionDetails) {
        this.connectionDetails = connectionDetails;
    }

    public IntHttpClient createConnection(@NotNull String url, @Nullable IntLogger logger) {
        if (logger == null) {
            logger = new SilentIntLogger();
        }
        if (ProxyUtil.shouldIgnoreUrl(url, connectionDetails.getIgnoredProxyHostPatterns(), logger)) {
            return new IntHttpClient(
                logger,
                connectionDetails.getGson(),
                Math.toIntExact(connectionDetails.getTimeout()),
                connectionDetails.getAlwaysTrust(),
                ProxyInfo.NO_PROXY_INFO
            );
        } else {
            return new IntHttpClient(
                logger,
                connectionDetails.getGson(),
                Math.toIntExact(connectionDetails.getTimeout()),
                connectionDetails.getAlwaysTrust(),
                connectionDetails.getProxyInformation()
            );
        }
    }

}