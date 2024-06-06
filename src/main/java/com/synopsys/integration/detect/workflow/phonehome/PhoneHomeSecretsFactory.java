package com.synopsys.integration.detect.workflow.phonehome;

import com.google.gson.Gson;
import com.synopsys.integration.detect.configuration.DetectInfoUtility;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class PhoneHomeSecretsFactory {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final static String STATIC_RESOURCE_HOST = "https://static-content.app.blackduck.com";
    private final static String PRODUCTION_CREDENTIALS_PATH = "/detect/analytics/prod/creds.json";
    private final static String TEST_CREDENTIALS_PATH = "/detect/analytics/test/creds.json";

    public PhoneHomeSecrets getGa4Credentials() throws IOException, InterruptedException {
        String fileUrl = STATIC_RESOURCE_HOST;
        if (isProduction())
            fileUrl += PRODUCTION_CREDENTIALS_PATH;
        else
            fileUrl += TEST_CREDENTIALS_PATH;

        logger.debug("Downloading phone home credentials.");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fileUrl))
                .timeout(Duration.of(20, ChronoUnit.SECONDS))
                .GET()
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
        return new Gson().fromJson(response.body(), PhoneHomeSecrets.class);
    }

    private boolean isProduction() {
        String detectVersion = new DetectInfoUtility().createDetectInfo().getDetectVersion();
        return !(StringUtils.contains(detectVersion, "SIGQA")
                || StringUtils.contains(detectVersion, "SNAPSHOT"));
    }
}
