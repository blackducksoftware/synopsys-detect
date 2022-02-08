package com.synopsys.integration.detect.battery.docker;

import java.io.IOException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.docker.provider.BuildDockerImageProvider;
import com.synopsys.integration.detect.battery.docker.util.DetectCommandBuilder;
import com.synopsys.integration.detect.battery.docker.util.DetectDockerTestRunner;
import com.synopsys.integration.detect.battery.docker.util.DockerAssertions;
import com.synopsys.integration.detect.configuration.DetectProperties;

@Tag("integration")
public class ImpactTest {
    @Test
    void offlineImpact() throws IOException {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("detect-impact-test", "detect-impact-test:1.0.0")) {
            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("Impact.dockerfile"));

            DetectCommandBuilder commandBuilder = DetectCommandBuilder.withOfflineDefaults().defaultDirectories(test);
            commandBuilder.property(DetectProperties.DETECT_TOOLS, "IMPACT_ANALYSIS");
            commandBuilder.property(DetectProperties.DETECT_IMPACT_ANALYSIS_ENABLED, "true");
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            dockerAssertions.successfulTool("IMPACT_ANALYSIS");
            dockerAssertions.logContainsPattern("Vulnerability Impact Analysis generated report at /opt/results/output/runs/.*/impact-analysis/external-method-uses.bdmu");
            dockerAssertions.successfulOperation("Generate Impact Analysis File");
        }
    }

    @Test
    void impactOutputPath() throws IOException {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("detect-impact-output-path-test", "detect-impact-test:1.0.0")) {
            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("Impact.dockerfile"));

            DetectCommandBuilder commandBuilder = DetectCommandBuilder.withOfflineDefaults().defaultDirectories(test);
            commandBuilder.property(DetectProperties.DETECT_TOOLS, "IMPACT_ANALYSIS");
            commandBuilder.property(DetectProperties.DETECT_IMPACT_ANALYSIS_ENABLED, "true");
            commandBuilder.property(DetectProperties.DETECT_IMPACT_ANALYSIS_OUTPUT_PATH, "/tmp");
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            dockerAssertions.logContains("Vulnerability Impact Analysis generated report at /tmp/external-method-uses.bdmu");
        }
    }
}
