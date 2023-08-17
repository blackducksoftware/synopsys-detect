package com.synopsys.integration.detect.battery.docker;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

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

    private static final String[] GO_VERSIONS_TO_TEST = new String[] {
        "1.16.5",
        "1.17.5",
        "1.18.5",
        "1.19.6",
        "1.20.4"
    };

    private static final String PROJECT_NAME = "go-mod-docker";

//    @Test
//    void goModExecutablesTest() throws IOException, IntegrationException {
//        for (String goVersion : GO_VERSIONS_TO_TEST) {
//            goModSpecificExecutableTest(goVersion);
//        }
//    }
//
    private static Stream<String> provideGoVersionsToTest() {
        return Arrays.stream(GO_VERSIONS_TO_TEST);
    }

    @ParameterizedTest
    @MethodSource("provideGoVersionsToTest")
    public void goModSpecificExecutableTest(String goVersion) throws IntegrationException, IOException {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("go-mod-executables-test", "go-mod-executables-test:" + goVersion)) {

            Map<String, String> goModDockerfileArgs = new HashMap<>();
            goModDockerfileArgs.put("goVersion", goVersion);

            BuildDockerImageProvider buildDockerImageProvider = BuildDockerImageProvider.forDockerfilResourceNamed("GoModExecutables.dockerfile");
            buildDockerImageProvider.setBuildArgs(goModDockerfileArgs);
            test.withImageProvider(buildDockerImageProvider);

            // Set up blackduck connection and environment
            String projectVersion = PROJECT_NAME + "-" + goVersion;
            BlackDuckTestConnection blackDuckTestConnection = BlackDuckTestConnection.fromEnvironment();
            BlackDuckAssertions blackduckAssertions = blackDuckTestConnection.projectVersionAssertions(PROJECT_NAME, projectVersion);
            blackduckAssertions.emptyOnBlackDuck();

            // Build command with BlackDuck config
            DetectCommandBuilder commandBuilder = new DetectCommandBuilder().defaults().defaultDirectories(test);
            commandBuilder.connectToBlackDuck(blackDuckTestConnection);
            commandBuilder.projectNameVersion(blackduckAssertions);
            commandBuilder.waitForResults();

            // Set up Detect properties
            commandBuilder.property(DetectProperties.DETECT_TOOLS, DetectTool.DETECTOR.toString());
            commandBuilder.property(DetectProperties.DETECT_GO_PATH, "/usr/local/go" + goVersion + "/go/bin/go");
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            // Detect specific assertions
            dockerAssertions.successfulDetectorType(DetectorType.GO_MOD.toString());
            dockerAssertions.atLeastOneBdioFile();

            // Blackduck specific assertions
            String codeLocationName = PROJECT_NAME + "/" + projectVersion + " bdio";
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
