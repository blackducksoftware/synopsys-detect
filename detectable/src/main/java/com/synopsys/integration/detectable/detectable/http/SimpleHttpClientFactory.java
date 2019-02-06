package com.synopsys.integration.detectable.detectable.http;

import com.synopsys.integration.log.SilentIntLogger;
import com.synopsys.integration.rest.client.IntHttpClient;
import com.synopsys.integration.rest.proxy.ProxyInfo;

public class SimpleHttpClientFactory implements HttpClientFactory {
    public IntHttpClient createHttpClient(String url) {
        return new IntHttpClient(new SilentIntLogger(), 0, true, ProxyInfo.NO_PROXY_INFO);
    }
}
