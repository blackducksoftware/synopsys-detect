package com.synopsys.integration.detect.battery.docker;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.docker.integration.BlackDuckAssertions;
import com.synopsys.integration.detect.battery.docker.integration.BlackDuckTestConnection;
import com.synopsys.integration.detect.battery.docker.provider.BuildDockerImageProvider;
import com.synopsys.integration.detect.battery.docker.util.DetectCommandBuilder;
import com.synopsys.integration.detect.battery.docker.util.DetectDockerTestRunner;
import com.synopsys.integration.detect.battery.docker.util.DockerAssertions;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.exception.IntegrationException;

//@Tag("integration")
public class GoModTest {

    @Test
    public void goModTest1() throws IntegrationException, IOException {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("go-mod-executables-test", "go-mod-executables-test:1.16.6")) {

            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("GoMod1.16.6.dockerfile"));

            // Set up blackduck connection and environment
            BlackDuckTestConnection blackDuckTestConnection = BlackDuckTestConnection.fromEnvironment();
            BlackDuckAssertions blackduckAssertions = blackDuckTestConnection.projectVersionAssertions("go-mod-docker", "go-mod-docker-version");
            blackduckAssertions.emptyOnBlackDuck();

            // Build command with BlackDuck config
            DetectCommandBuilder commandBuilder = new DetectCommandBuilder().defaults().defaultDirectories(test);
            commandBuilder.connectToBlackDuck(blackDuckTestConnection);
            commandBuilder.projectNameVersion(blackduckAssertions);
            commandBuilder.waitForResults();

            // Set up Detect properties
            commandBuilder.property(DetectProperties.DETECT_TOOLS, "DETECTOR");
            commandBuilder.property(DetectProperties.DETECT_GO_PATH, "/usr/local/go1.16.6/go/bin/go");
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            // Detect specific assertions
            dockerAssertions.successfulDetectorType(DetectorType.GO_MOD.toString());
            dockerAssertions.atLeastOneBdioFile();

            // Blackduck specific assertions
            String codeLocationName = "go-mod-docker/go-mod-docker-version bdio";
            blackduckAssertions.hasCodeLocations(codeLocationName);
            validateComponentsForSampleGoProject(blackduckAssertions);
        }
    }

    // These are all components in the go.mod file of the test project "https://github.com/Masterminds/squirrel.git (v1.5.4)" that should appear on the BOM on BlackDuck
    // If ever updating the above test project, ensure to update the component list below accordingly
    private void validateComponentsForSampleGoProject(BlackDuckAssertions blackduckAssertions) throws IntegrationException {
        blackduckAssertions.hasComponents("Go Testify");
        blackduckAssertions.hasComponents("go-spew");
        blackduckAssertions.hasComponents("lann-builder");
        blackduckAssertions.hasComponents("lann-ps");
        blackduckAssertions.hasComponents("pmezard-go-difflib");
    }

}
