package com.synopsys.integration.detect.battery.docker.integration;

import java.io.IOException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.docker.provider.BuildDockerImageProvider;
import com.synopsys.integration.detect.battery.docker.util.DetectCommandBuilder;
import com.synopsys.integration.detect.battery.docker.util.DetectDockerTestRunner;
import com.synopsys.integration.detect.battery.docker.util.DockerAssertions;
import com.synopsys.integration.exception.IntegrationException;

@Tag("integration")
public class DetectOnAlertTest {
    @Test
    @Disabled
        //currently adds a lot of time, for I expect little value. If useful, feel free to re-enable.
    void detectOnAlert() throws IOException, IntegrationException {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("detect-on-alert", "alert-6.5.0:1.0.1")) {
            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("Alert-6.5.0.dockerfile"));

            BlackDuckTestConnection blackDuckTestConnection = BlackDuckTestConnection.fromEnvironment();
            BlackDuckAssertions blackduckAssertions = blackDuckTestConnection.projectVersionAssertions("blackduck-alert", "6.5.1-SNAPSHOT");
            blackduckAssertions.emptyOnBlackDuck();

            DetectCommandBuilder commandBuilder = new DetectCommandBuilder().defaults().defaultDirectories(test);
            commandBuilder.connectToBlackDuck(blackDuckTestConnection);
            commandBuilder.waitForResults();

            DockerAssertions dockerAssertions = test.run(commandBuilder);

            dockerAssertions.projectVersion(blackduckAssertions.getProjectNameVersion());

            blackduckAssertions.codeLocationCount(16);
        }
    }

}
