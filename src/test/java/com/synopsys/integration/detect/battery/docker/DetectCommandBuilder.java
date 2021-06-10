package com.synopsys.integration.detect.battery.docker;

import java.util.Map;

import com.synopsys.integration.detect.configuration.DetectProperty;

public class DetectCommandBuilder {
    Map<String, String> properties;

    public String buildCommand() {
        return "";
    }

    public void property(String key, String value) {
        properties.put(key, value);
    }

    public void property(DetectProperty<?> property, String value) {
        property(property.getProperty().getKey(), value);
    }

}
