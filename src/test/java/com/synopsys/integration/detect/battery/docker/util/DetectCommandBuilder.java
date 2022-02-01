package com.synopsys.integration.detect.battery.docker.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.detect.battery.docker.integration.BlackDuckAssertions;
import com.synopsys.integration.detect.battery.docker.integration.BlackDuckTestConnection;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.util.NameVersion;

public class DetectCommandBuilder {
    Map<String, String> properties = new HashMap<>();

    public static DetectCommandBuilder withOfflineDefaults() {
        return new DetectCommandBuilder().offlineDefaults();
    }

    public String buildCommand() {
        StringBuilder builder = new StringBuilder();
        properties.forEach((key, value) -> {
            builder.append(" --").append(key).append("=").append(value);
        });
        return builder.toString();
    }

    public String[] buildArguments() {
        List<String> arguments = new ArrayList<>();
        properties.forEach((key, value) -> {
            arguments.add(String.format("--%s=%s", key, value));
        });
        return arguments.toArray(new String[0]);
    }

    public DetectCommandBuilder property(String key, String value) {
        properties.put(key, value);
        return this;
    }

    public DetectCommandBuilder property(Property property, String value) {
        property(property.getKey(), value);
        return this;
    }

    public DetectCommandBuilder connectToBlackDuck(String blackduckUrl, String apiToken, boolean trustCert) {
        property(DetectProperties.BLACKDUCK_URL, blackduckUrl);
        property(DetectProperties.BLACKDUCK_API_TOKEN, apiToken);
        property(DetectProperties.BLACKDUCK_TRUST_CERT, trustCert ? "true" : "false");
        return this;
    }

    public DetectCommandBuilder defaults() {
        property(DetectProperties.DETECT_CLEANUP, "false");
        property(DetectProperties.LOGGING_LEVEL_COM_SYNOPSYS_INTEGRATION, "DEBUG"); //Needed to assert on operations in the logs.
        return this;
    }

    public DetectCommandBuilder offlineDefaults() {
        defaults();
        property(DetectProperties.BLACKDUCK_OFFLINE_MODE, "true");
        return this;
    }

    public DetectCommandBuilder tools(DetectTool... detectTools) {
        property(DetectProperties.DETECT_TOOLS, StringUtils.join(detectTools, ","));
        return this;
    }

    public DetectCommandBuilder waitForResults() {
        property(DetectProperties.DETECT_WAIT_FOR_RESULTS, "true");
        return this;
    }

    public void projectNameVersion(NameVersion projectNameVersion) {
        property(DetectProperties.DETECT_PROJECT_NAME, projectNameVersion.getName());
        property(DetectProperties.DETECT_PROJECT_VERSION_NAME, projectNameVersion.getVersion());
    }

    public DetectCommandBuilder connectToBlackDuck(BlackDuckTestConnection blackDuckTestConnection) {
        connectToBlackDuck(blackDuckTestConnection.getBlackduckUrl(), blackDuckTestConnection.getBlackduckApiToken(), blackDuckTestConnection.trustCert());
        return this;
    }

    public DetectCommandBuilder projectNameVersion(BlackDuckAssertions projectUtil) {
        projectNameVersion(projectUtil.getProjectNameVersion());
        return this;
    }

    public DetectCommandBuilder defaultDirectories(DetectDockerTestRunner test) throws IOException {
        property(DetectProperties.DETECT_BDIO_OUTPUT_PATH, test.directories().bdioBinding());
        property(DetectProperties.DETECT_OUTPUT_PATH, test.directories().detectOutputPathBinding());
        return this;
    }

    public void debug() {
        property(DetectProperties.LOGGING_LEVEL_COM_SYNOPSYS_INTEGRATION, "TRACE");
    }
}
