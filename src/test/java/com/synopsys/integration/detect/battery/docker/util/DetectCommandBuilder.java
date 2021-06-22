package com.synopsys.integration.detect.battery.docker.util;

import java.util.HashMap;
import java.util.Map;

import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.DetectProperty;

public class DetectCommandBuilder {
    Map<String, String> properties = new HashMap<>();

    public static DetectCommandBuilder withDefaults() {
        return new DetectCommandBuilder().defaults();
    }

    public String buildCommand() {
        StringBuilder builder = new StringBuilder();
        properties.forEach((key, value) -> {
            builder.append(" --").append(key).append("=").append(value);
        });
        return builder.toString();
    }

    public DetectCommandBuilder property(String key, String value) {
        properties.put(key, value);
        return this;
    }

    public DetectCommandBuilder property(DetectProperty<?> property, String value) {
        property(property.getProperty().getKey(), value);
        return this;
    }

    public DetectCommandBuilder connectToBlackDuck(final String blackduckUrl, final String apiToken, final boolean trustCert) {
        property(DetectProperties.BLACKDUCK_URL, blackduckUrl);
        property(DetectProperties.BLACKDUCK_API_TOKEN, apiToken);
        property(DetectProperties.BLACKDUCK_TRUST_CERT, trustCert ? "true" : "false");
        return this;
    }

    public DetectCommandBuilder defaults() {
        property(DetectProperties.BLACKDUCK_OFFLINE_MODE, "true");
        property(DetectProperties.DETECT_CLEANUP, "false");
        property(DetectProperties.LOGGING_LEVEL_COM_SYNOPSYS_INTEGRATION, "DEBUG");
        return this;
    }
}
