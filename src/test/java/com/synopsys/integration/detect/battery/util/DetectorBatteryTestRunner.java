package com.synopsys.integration.detect.battery.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.detect.configuration.DetectProperties;

public final class DetectorBatteryTestRunner extends BatteryTestRunner {
    private String toolsValue = "DETECTOR";
    private final List<String> additionalProperties = new ArrayList<>();

    public DetectorBatteryTestRunner(String name) {
        super(name);
    }

    public DetectorBatteryTestRunner(String testName, String resourcePrefix) {
        super(testName, resourcePrefix);
    }

    public void withToolsValue(String toolsValue) {
        this.toolsValue = toolsValue;
    }

    public void property(Property property, String value) {
        property(property.getKey(), value);
    }

    public void property(String property, String value) {
        additionalProperties.add("--" + property + "=" + value);
    }

    @Override
    public List<String> generateArguments() throws IOException {
        List<String> detectArguments = new ArrayList<>();
        Map<Property, String> properties = new HashMap<>();

        properties.put(DetectProperties.DETECT_TOOLS, toolsValue);
        properties.put(DetectProperties.BLACKDUCK_OFFLINE_MODE, "true");
        properties.put(DetectProperties.DETECT_OUTPUT_PATH, batteryContext.getOutputDirectory().getCanonicalPath());
        properties.put(DetectProperties.DETECT_BDIO_OUTPUT_PATH, batteryContext.getBdioDirectory().getCanonicalPath());
        properties.put(DetectProperties.DETECT_BDIO_FILE_NAME, batteryContext.getBdioFileName());
        properties.put(DetectProperties.DETECT_CLEANUP, "false");
        properties.put(DetectProperties.LOGGING_LEVEL_COM_SYNOPSYS_INTEGRATION, "INFO"); // Leave at INFO for Travis. Long logs cause build to fail.
        properties.put(DetectProperties.DETECT_SOURCE_PATH, batteryContext.getSourceDirectory().getCanonicalPath());
        for (Map.Entry<Property, String> entry : properties.entrySet()) {
            detectArguments.add("--" + entry.getKey().getKey() + "=" + entry.getValue());
        }

        detectArguments.addAll(additionalProperties);

        return detectArguments;
    }

    public void enableDiagnostics() {
        property(DetectProperties.DETECT_DIAGNOSTIC, "true");
    }
}
