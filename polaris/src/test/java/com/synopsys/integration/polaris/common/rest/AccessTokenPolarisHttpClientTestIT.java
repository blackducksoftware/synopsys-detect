package com.synopsys.integration.polaris.common.rest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.LogLevel;
import com.synopsys.integration.log.PrintStreamIntLogger;
import com.synopsys.integration.rest.HttpMethod;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.client.ConnectionResult;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.response.Response;
import com.synopsys.integration.rest.support.AuthenticationSupport;

public class AccessTokenPolarisHttpClientTestIT {
    public static final String ENV_POLARIS_URL = "POLARIS_URL";
    public static final String ENV_POLARIS_ACCESS_TOKEN = "POLARIS_ACCESS_TOKEN";

    private static final String VALID_SPEC = "/api/common/v0/branches?page[offset]=0&page[limit]=10";
    private static final String VALID_MIME_TYPE = "application/vnd.api+json";
    private static final String INVALID_MIME_TYPE = "application/x-www-form-urlencoded";

    private HttpUrl baseUrl;
    private String accessToken;
    private AuthenticationSupport authenticationSupport;
    private Gson gson;

    @BeforeEach
    public void setup() throws IntegrationException {
        String url = System.getenv(AccessTokenPolarisHttpClientTestIT.ENV_POLARIS_URL);
        assumeTrue(StringUtils.isNotBlank(url));
        baseUrl = new HttpUrl(url);
        accessToken = System.getenv(AccessTokenPolarisHttpClientTestIT.ENV_POLARIS_ACCESS_TOKEN);
        authenticationSupport = new AuthenticationSupport();
        gson = new Gson();
    }

    @Test
    public void validRequestTest() throws IntegrationException, IOException {
        assumeTrue(StringUtils.isNotBlank(accessToken));

        AccessTokenPolarisHttpClient httpClient = new AccessTokenPolarisHttpClient(new PrintStreamIntLogger(System.out, LogLevel.INFO), 300, true, ProxyInfo.NO_PROXY_INFO, baseUrl, accessToken, gson,
            authenticationSupport);

        HttpUrl requestUrl = httpClient.appendToPolarisUrl(AccessTokenPolarisHttpClientTestIT.VALID_SPEC);
        Request request = new Request.Builder().method(HttpMethod.GET).url(requestUrl).acceptMimeType(AccessTokenPolarisHttpClientTestIT.VALID_MIME_TYPE).build();
        try (Response response = httpClient.execute(request)) {
            assertTrue(response.isStatusCodeSuccess(), "Status code was not in the SUCCESS range");
            System.out.println(response.getContentString());
        }
    }

    @Test
    public void testSuccessConnectionResult() {
        assumeTrue(StringUtils.isNotBlank(accessToken));

        AccessTokenPolarisHttpClient httpClient = new AccessTokenPolarisHttpClient(new PrintStreamIntLogger(System.out, LogLevel.INFO), 300, true, ProxyInfo.NO_PROXY_INFO, baseUrl, accessToken, gson,
            authenticationSupport);
        ConnectionResult connectionResult = httpClient.attemptConnection();
        assertTrue(connectionResult.isSuccess());
    }

    @Test
    public void testFailureConnectionResult() {
        assumeTrue(StringUtils.isNotBlank(accessToken));

        AccessTokenPolarisHttpClient httpClient = new AccessTokenPolarisHttpClient(new PrintStreamIntLogger(System.out, LogLevel.INFO), 300, true, ProxyInfo.NO_PROXY_INFO, baseUrl, accessToken + "make it bad", gson,
            authenticationSupport);
        ConnectionResult connectionResult = httpClient.attemptConnection();
        assertTrue(connectionResult.isFailure());
    }

    @Test
    public void invalidMimeTypeTest() throws IntegrationException, IOException {
        assumeTrue(StringUtils.isNotBlank(accessToken));

        AccessTokenPolarisHttpClient httpClient = new AccessTokenPolarisHttpClient(new PrintStreamIntLogger(System.out, LogLevel.INFO), 300, true, ProxyInfo.NO_PROXY_INFO, baseUrl, accessToken, gson,
            authenticationSupport);

        HttpUrl requestUrl = httpClient.appendToPolarisUrl(AccessTokenPolarisHttpClientTestIT.VALID_SPEC);
        Request request = new Request.Builder().method(HttpMethod.GET).url(requestUrl).acceptMimeType(AccessTokenPolarisHttpClientTestIT.INVALID_MIME_TYPE).build();
        try (Response response = httpClient.execute(request)) {
            assertTrue(response.isStatusCodeError(), "Status code was not an error");
        }
    }

}
