package com.synopsys.integration.detect.battery.docker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.detect.battery.util.DockerTestAssertions;
import com.synopsys.integration.detect.configuration.DetectProperties;

@Tag("docker")
public class ShortFormExampleDockerBattery {
    @Test
    void run() throws IOException, InterruptedException {
        DetectDockerTest test = new DetectDockerTest("short-form-test", "detect-docker-short-form:1.0.0", "Yarn_Berry.dockerfile");
        DockerTestDirectories directories = test.setup();
        DockerTestAssertions result = test.run(arguments(directories.getContainerOutput(), directories.getContainerOutput(), "/opt/project/src"));
        result.atLeastOneBdioFile();
        result.successfulDetectorType("DETECT");
    }

    private String arguments(String outputDirectory, String bdioDirectory, String sourceDirectory) {
        Map<Property, String> properties = new HashMap<>();
        properties.put(DetectProperties.DETECT_TOOLS.getProperty(), "DETECTOR");
        properties.put(DetectProperties.BLACKDUCK_OFFLINE_MODE.getProperty(), "true");
        properties.put(DetectProperties.DETECT_OUTPUT_PATH.getProperty(), outputDirectory);
        properties.put(DetectProperties.DETECT_BDIO_OUTPUT_PATH.getProperty(), bdioDirectory);
        properties.put(DetectProperties.DETECT_CLEANUP.getProperty(), "false");
        properties.put(DetectProperties.LOGGING_LEVEL_COM_SYNOPSYS_INTEGRATION.getProperty(), "INFO"); // Leave at INFO for Travis. Long logs cause build to fail.
        properties.put(DetectProperties.DETECT_SOURCE_PATH.getProperty(), sourceDirectory);

        StringBuilder builder = new StringBuilder();
        properties.forEach((key, value) -> {
            builder.append(" --").append(key.getKey()).append("=").append(value);
        });
        return builder.toString();
    }

}
