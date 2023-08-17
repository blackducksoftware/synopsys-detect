package com.synopsys.integration.detect.battery.docker;

import java.io.IOException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.docker.integration.BlackDuckAssertions;
import com.synopsys.integration.detect.battery.docker.integration.BlackDuckTestConnection;
import com.synopsys.integration.detect.battery.docker.provider.BuildDockerImageProvider;
import com.synopsys.integration.detect.battery.docker.util.DetectCommandBuilder;
import com.synopsys.integration.detect.battery.docker.util.DetectDockerTestRunner;
import com.synopsys.integration.detect.battery.docker.util.DockerAssertions;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.exception.IntegrationException;

//@Tag("integration")
public class GoModTest {

    void goModOfflineTest() throws IOException {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("go-mod-executables", "go-mod-executables:1.0.0")) {
            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("GoModExecutables.dockerfile"));

            DetectCommandBuilder commandBuilder = DetectCommandBuilder.withOfflineDefaults().defaultDirectories(test);
            commandBuilder.property(DetectProperties.DETECT_TOOLS, "DETECTOR");
            commandBuilder.property(DetectProperties.DETECT_GO_PATH, "/usr/local/go1.16.6/go/bin/go");
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            dockerAssertions.successfulDetectorType("GO_MOD");
            dockerAssertions.atLeastOneBdioFile();
        }
    }

    private void validateComponentsForSampleGoProject(BlackDuckAssertions blackduckAssertions) throws IntegrationException {
        blackduckAssertions.hasComponents("Go Testify");
        blackduckAssertions.hasComponents("go-spew");
        blackduckAssertions.hasComponents("lann-builder");
        blackduckAssertions.hasComponents("lann-ps");
        blackduckAssertions.hasComponents("pmezard-go-difflib");
    }

    @Test
    void goModExecutablesTest() throws IOException, IntegrationException {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("go-mod-executables", "go-mod-executables:1.0.0")) {
            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("GoModExecutables.dockerfile"));

            // Set up blackduck connection and environment
            BlackDuckTestConnection blackDuckTestConnection = BlackDuckTestConnection.fromEnvironment();
            BlackDuckAssertions blackduckAssertions = blackDuckTestConnection.projectVersionAssertions("go-mod-docker", "go-mod-docker-version");
            blackduckAssertions.emptyOnBlackDuck();

            DetectCommandBuilder commandBuilder = new DetectCommandBuilder().defaults().defaultDirectories(test);
            commandBuilder.connectToBlackDuck(blackDuckTestConnection);
            commandBuilder.projectNameVersion(blackduckAssertions);
            commandBuilder.waitForResults();

            // Set up Detect properties
            commandBuilder.property(DetectProperties.DETECT_TOOLS, "DETECTOR");
            commandBuilder.property(DetectProperties.DETECT_GO_PATH, "/usr/local/go1.16.6/go/bin/go");
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            // Detect specific assertions
            dockerAssertions.successfulDetectorType("GO_MOD");
            dockerAssertions.atLeastOneBdioFile();

            // Blackduck specific assertions
            blackduckAssertions.hasCodeLocations(
                "go-mod-docker/go-mod-docker-version bdio"
            );
            validateComponentsForSampleGoProject(blackduckAssertions);

        }
    }
}
