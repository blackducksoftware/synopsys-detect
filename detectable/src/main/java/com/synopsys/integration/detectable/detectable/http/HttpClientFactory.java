package com.synopsys.integration.detectable.detectable.http;

import com.synopsys.integration.rest.client.IntHttpClient;

public interface HttpClientFactory {
    IntHttpClient createHttpClient(String url);
}
