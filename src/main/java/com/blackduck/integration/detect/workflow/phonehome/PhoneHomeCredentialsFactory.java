package com.blackduck.integration.detect.workflow.phonehome;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.blackduck.integration.detect.configuration.DetectInfoUtility;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.SilentIntLogger;
import com.synopsys.integration.rest.HttpMethod;
import com.synopsys.integration.rest.client.IntHttpClient;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.rest.response.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhoneHomeCredentialsFactory {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final static String CREDENTIALS_PATH = "https://static-content.app.blackduck.com/detect/analytics/creds.json";
    private final static String TEST_CREDENTIALS_PATH = "https://static-content.saas-staging.blackduck.com/detect/analytics/creds.json";
    private final static int TIMEOUT_IN_SECONDS = 20;
    private IntHttpClient httpClient;
    private Gson gson;

    public PhoneHomeCredentialsFactory() {
        gson = new Gson();
        httpClient = new IntHttpClient(
            new SilentIntLogger(),
            gson,
            TIMEOUT_IN_SECONDS,
            true,
            ProxyInfo.NO_PROXY_INFO
        );
    }

    public PhoneHomeCredentials getGa4Credentials()  throws JsonSyntaxException, IntegrationException {
        String fileUrl = CREDENTIALS_PATH;
        if (isTestEnvironment()) {
            fileUrl = TEST_CREDENTIALS_PATH;
            logger.debug("Phone home is operational for a test environment.");
        }
        logger.debug("Downloading phone home credentials.");
        RequestBuilder createRequestBuilder = httpClient.createRequestBuilder(HttpMethod.GET);
        HttpUriRequest request = createRequestBuilder
            .setUri(fileUrl)
            .build();
        Response response = httpClient.execute(request);
        return gson.fromJson(response.getContentString(), PhoneHomeCredentials.class);
    }

    private boolean isTestEnvironment() {
        String detectVersion = new DetectInfoUtility().createDetectInfo().getDetectVersion();
        return (StringUtils.contains(detectVersion, "SIGQA")
                || StringUtils.contains(detectVersion, "SNAPSHOT"));
    }
}
