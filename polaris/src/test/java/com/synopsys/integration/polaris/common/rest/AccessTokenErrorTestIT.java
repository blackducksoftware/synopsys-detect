package com.synopsys.integration.polaris.common.rest;

import java.io.IOException;

import org.apache.http.client.methods.HttpUriRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.LogLevel;
import com.synopsys.integration.log.PrintStreamIntLogger;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.RestConstants;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.rest.response.Response;
import com.synopsys.integration.rest.support.AuthenticationSupport;

public class AccessTokenErrorTestIT {
    @Test
    public void unauthorizedTest() throws IntegrationException, IOException {
        AccessTokenPolarisHttpClient httpClient = new AccessTokenPolarisHttpClient(new PrintStreamIntLogger(System.out, LogLevel.INFO), 300, true, ProxyInfo.NO_PROXY_INFO, new HttpUrl("http://www.blackducksoftware.com"), "garbage token",
            new Gson(), new AuthenticationSupport());

        String authHeader = "Authorization";
        HttpUriRequest request = Mockito.mock(HttpUriRequest.class);
        Mockito.when(request.containsHeader(authHeader)).thenReturn(true);
        Mockito.doNothing().when(request).removeHeaders(authHeader);

        Response response = Mockito.mock(Response.class);
        Mockito.when(response.getStatusCode()).thenReturn(RestConstants.UNAUTHORIZED_401);

        httpClient.handleErrorResponse(request, response);
        Mockito.verify(request, Mockito.times(1)).removeHeaders(authHeader);
        Mockito.verify(response, Mockito.times(1)).getStatusCode();
    }

}
