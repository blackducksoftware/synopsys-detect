package com.synopsys.integration.detect.battery.docker;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Tag;
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
public class PipenvTest {

    private static final String[] PIPENV_VERSIONS_TO_TEST = new String[] { "2024.0.1" };
    public static String ARTIFACTORY_URL = "https://artifactory.internal.synopsys.com:443";

    private static final String PROJECT_NAME = "pipenv-docker-test-project";

    private static Stream<String> providePipenvVersionsToTest() {
        return Arrays.stream(PIPENV_VERSIONS_TO_TEST);
    }

    @ParameterizedTest
    @MethodSource("providePipenvVersionsToTest")
    public void pipenvExecutableTest(String pipenvVersion) throws IntegrationException, IOException {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("pipenv-docker-test   ", "pipenv-docker-test:" + pipenvVersion)) {

            Map<String, String> pipenvDockerfileArgs = new HashMap<>();
            pipenvDockerfileArgs.put("PIPENV_VERSION", pipenvVersion);
            pipenvDockerfileArgs.put("ARTIFACTORY_URL", ARTIFACTORY_URL);

            BuildDockerImageProvider buildDockerImageProvider = BuildDockerImageProvider.forDockerfilResourceNamed("Pipenv.dockerfile");
            buildDockerImageProvider.setBuildArgs(pipenvDockerfileArgs);
            test.withImageProvider(buildDockerImageProvider);

            // Set up blackduck connection and environment
            String projectVersion = PROJECT_NAME + "-" + pipenvVersion;
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
            commandBuilder.property(DetectProperties.DETECT_PIPENV_PATH, "/usr/local/bin/pipenv");
            commandBuilder.property(DetectProperties.DETECT_PYTHON_PATH, "/usr/bin/python3");
            commandBuilder.property(DetectProperties.DETECT_ACCURACY_REQUIRED, "NONE");
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            // Detect specific assertions
            dockerAssertions.successfulDetectorType(DetectorType.PIP.toString());
            dockerAssertions.atLeastOneBdioFile();

            // Blackduck specific assertions
            validateComponentsForSamplePipProject(blackduckAssertions);
        }
    }

    // These are all components in the requirements.txt file of the test project "<insert-test-project-link>" that should appear on the BOM on BlackDuck
    // If ever updating the above test project, ensure to update the component list below accordingly
    private void validateComponentsForSamplePipProject(BlackDuckAssertions blackduckAssertions) throws IntegrationException {
        blackduckAssertions.checkComponentVersionExists("Jinja", "3.0.3");
        blackduckAssertions.checkComponentVersionExists("urllib3", "1.26.8");
        blackduckAssertions.checkComponentVersionExists("MarkupSafe", "2.0.1");
        blackduckAssertions.checkComponentVersionExists("idna", "3.3");
        blackduckAssertions.checkComponentVersionExists("Werkzeug", "2.0.2");
    }
}
