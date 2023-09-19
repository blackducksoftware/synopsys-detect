package com.synopsys.integration.detect;

import com.google.gson.Gson;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.rest.response.Response;
import org.apache.http.client.methods.HttpUriRequest;

public class UpdaterHttpClient extends com.synopsys.integration.rest.client.IntHttpClient {
    
    public UpdaterHttpClient(IntLogger logger, Gson gson, int timeoutInSeconds, boolean alwaysTrustServerCertificate, ProxyInfo proxyInfo) {
        super(logger, gson, timeoutInSeconds, alwaysTrustServerCertificate, proxyInfo);
    }
    
    @Override
    protected void handleErrorResponse(HttpUriRequest request, Response response) {
        // TODO - Handle errors here
    }
    
}