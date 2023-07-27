package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.synopsys.integration.detect.battery.docker.provider.BuildDockerImageProvider;
import com.synopsys.integration.detect.battery.docker.util.DetectCommandBuilder;
import com.synopsys.integration.detect.battery.docker.util.DetectDockerTestRunner;
import com.synopsys.integration.detect.battery.docker.util.DockerAssertions;
import com.synopsys.integration.detect.configuration.DetectProperties;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Disabled
@Tag("integration")
public class GenerateComponentLocationAnalysisOperationIT {
    @Test
    void testOfflinePkgMngrScan_analysisEnabled() throws IOException {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("component-location-analysis-test", "gradle-simple:1.0.0")) {
            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("SimpleGradle.dockerfile"));

            DetectCommandBuilder commandBuilder = DetectCommandBuilder.withOfflineDefaults().defaultDirectories(test);
            commandBuilder.property(DetectProperties.DETECT_COMPONENT_LOCATION_ANALYSIS_ENABLED, "true");
            commandBuilder.property(DetectProperties.BLACKDUCK_OFFLINE_MODE, "true");
            commandBuilder.property(DetectProperties.BLACKDUCK_OFFLINE_MODE_FORCE_BDIO, "true");
            commandBuilder.property(DetectProperties.DETECT_INCLUDED_DETECTOR_TYPES, "DETECTOR");

            DockerAssertions dockerAssertions = test.run(commandBuilder);

            dockerAssertions.successfulOperation("Generating Component Location Analysis File for All Components");
        }
    }
    @Test
    void onlineRapidPkgMngrScan_analysisEnabled() throws IOException {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("component-location-analysis-test", "gradle-simple:1.0.0")) {
            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("SimpleGradle.dockerfile"));

            DetectCommandBuilder commandBuilder = DetectCommandBuilder.withOfflineDefaults().defaultDirectories(test);
            commandBuilder.property(DetectProperties.DETECT_COMPONENT_LOCATION_ANALYSIS_ENABLED, "true");
            commandBuilder.property(DetectProperties.DETECT_BLACKDUCK_SCAN_MODE, "RAPID");

            DockerAssertions dockerAssertions = test.run(commandBuilder);

            dockerAssertions.successfulOperation("Generating Component Location Analysis File for Reported Components");
        }
    }
}
