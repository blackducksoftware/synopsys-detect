package com.synopsys.integration.detect.battery.docker.integration;

import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfigBuilder;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.dataservice.ProjectService;
import com.synopsys.integration.detect.workflow.blackduck.report.service.ReportService;
import com.synopsys.integration.log.BufferedIntLogger;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NameVersion;

public class BlackDuckTestConnection {
    public static final String BLACKDUCK_URL = "BLACKDUCK_URL";
    public static final String BLACKDUCK_API_TOKEN = "BLACKDUCK_API_TOKEN";

    private final BlackDuckServicesFactory blackDuckServicesFactory;
    private final String blackduckUrl;
    private final String blackduckApiToken;
    private static final boolean trustCert = true; //for now we just force trust cert. we could read this from the env like the url or token - jp

    private BlackDuckTestConnection(String blackduckUrl, String blackduckApiToken, BlackDuckServicesFactory blackDuckServicesFactory) {
        this.blackduckUrl = blackduckUrl;
        this.blackduckApiToken = blackduckApiToken;
        this.blackDuckServicesFactory = blackDuckServicesFactory;
    }

    public static BlackDuckTestConnection fromEnvironment() {
        Assertions.assertTrue(System.getenv().containsKey(BLACKDUCK_URL), "Integration tests require BLACKDUCK_URL is set in the environment");
        Assertions.assertTrue(System.getenv().containsKey(BLACKDUCK_API_TOKEN), "Integration tests require BLACKDUCK_API_TOKEN is set in the environment");

        String blackduckUrl = System.getenv().get(BLACKDUCK_URL);
        String blackduckApiToken = System.getenv().get(BLACKDUCK_API_TOKEN);

        BlackDuckServerConfigBuilder blackDuckServerConfigBuilder = BlackDuckServerConfig.newApiTokenBuilder();
        blackDuckServerConfigBuilder.setProperties(System.getenv().entrySet());
        blackDuckServerConfigBuilder.setUrl(blackduckUrl);
        blackDuckServerConfigBuilder.setApiToken(blackduckApiToken);
        blackDuckServerConfigBuilder.setTrustCert(trustCert);
        blackDuckServerConfigBuilder.setTimeoutInSeconds(5 * 60);

        BlackDuckServicesFactory blackDuckServicesFactory = blackDuckServerConfigBuilder.build().createBlackDuckServicesFactory(new BufferedIntLogger());
        return new BlackDuckTestConnection(blackduckUrl, blackduckApiToken, blackDuckServicesFactory);
    }

    public ReportService createReportService() {
        HttpUrl blackDuckUrl = blackDuckServicesFactory.getBlackDuckHttpClient().getBlackDuckUrl();
        IntegrationEscapeUtil integrationEscapeUtil = blackDuckServicesFactory.createIntegrationEscapeUtil();
        long reportServiceTimeout = 120 * 1000;
        return new ReportService(
            blackDuckServicesFactory.getGson(),
            blackDuckUrl,
            blackDuckServicesFactory.getBlackDuckApiClient(),
            blackDuckServicesFactory.getApiDiscovery(),
            new BufferedIntLogger(),
            integrationEscapeUtil,
            reportServiceTimeout
        );
    }

    public String getBlackduckUrl() {
        return blackduckUrl;
    }

    public String getBlackduckApiToken() {
        return blackduckApiToken;
    }

    public BlackDuckAssertions projectVersionAssertions(NameVersion projectNameVersion) {
        return new BlackDuckAssertions(blackDuckServicesFactory, projectNameVersion);
    }

    public BlackDuckAssertions projectVersionAssertions(String projectName, String projectVersion) {
        return projectVersionAssertions(new NameVersion(projectName, projectVersion));
    }

    public boolean trustCert() {
        return trustCert;
    }

    public ProjectService createProjectService() {
        return blackDuckServicesFactory.createProjectService();
    }

    public BlackDuckServicesFactory getBlackDuckServicesFactory() {
        return blackDuckServicesFactory;
    }
}
