package com.synopsys.integration.detect.battery.docker.integration;

import java.io.IOException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.docker.provider.BuildDockerImageProvider;
import com.synopsys.integration.detect.battery.docker.util.DetectCommandBuilder;
import com.synopsys.integration.detect.battery.docker.util.DetectDockerTestRunner;
import com.synopsys.integration.detect.battery.docker.util.DockerAssertions;
import com.synopsys.integration.exception.IntegrationException;

@Tag("integration")
public class SubProjectAggregateModeTest {

    // Once we're testing against Black Duck 2021.10+, we can verify that the correct project name/version
    // was created in Black Duck. But that doesn't work yet (in Black Duck).

    @Test
    void subProjectAggregateModeSmokeTest() throws IOException, IntegrationException {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("subproject-aggregate-mode", "detect-7.1.0:1.0.0")) {
            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("Detect-7.1.0.dockerfile"));

            BlackDuckTestConnection blackDuckTestConnection = BlackDuckTestConnection.fromEnvironment();
            BlackDuckAssertions blackduckAssertions = blackDuckTestConnection.projectVersionAssertions("subproject-aggregate-mode-docker", "happy-path");
            blackduckAssertions.emptyOnBlackDuck();

            DetectCommandBuilder commandBuilder = new DetectCommandBuilder().defaults().defaultDirectories(test);
            commandBuilder.connectToBlackDuck(blackDuckTestConnection);
            commandBuilder.projectNameVersion(blackduckAssertions);
            String bdioFilename = "testagg";
            commandBuilder.property("detect.bdio.file.name", bdioFilename);
            commandBuilder.property("detect.tools", "DETECTOR");

            DockerAssertions dockerAssertions = test.run(commandBuilder);

            dockerAssertions.successfulOperation("SubProject Aggregate");
            dockerAssertions.bdioFiles(1);
            dockerAssertions.bdioFileCreated(bdioFilename + ".bdio");
        }
    }
}
