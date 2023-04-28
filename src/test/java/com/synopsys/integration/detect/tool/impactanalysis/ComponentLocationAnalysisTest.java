package com.synopsys.integration.detect.tool.impactanalysis;

import com.synopsys.integration.detect.battery.docker.provider.BuildDockerImageProvider;
import com.synopsys.integration.detect.battery.docker.util.DetectCommandBuilder;
import com.synopsys.integration.detect.battery.docker.util.DetectDockerTestRunner;
import com.synopsys.integration.detect.battery.docker.util.DockerAssertions;
import com.synopsys.integration.detect.configuration.DetectProperties;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ComponentLocationAnalysisTest {
    @Test
    void offline() throws IOException {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("detect-location-output-path-test", "detect-location-test:1.0.0")) {
            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("Impact.dockerfile"));

            DetectCommandBuilder commandBuilder = DetectCommandBuilder.withOfflineDefaults().defaultDirectories(test);
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            dockerAssertions.logContains("Components with declaration location analysis generated report at /tmp/external-method-uses.bdmu");
        }
    }

    @Test
    void rapid() throws IOException {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("detect-location-output-path-test", "detect-location-test:1.0.0")) {
            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("Impact.dockerfile"));

            DetectCommandBuilder commandBuilder = DetectCommandBuilder.withOfflineDefaults().defaultDirectories(test);
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            dockerAssertions.logContains("Components with declaration location analysis generated report at /tmp/external-method-uses.bdmu");
        }
    }
}
