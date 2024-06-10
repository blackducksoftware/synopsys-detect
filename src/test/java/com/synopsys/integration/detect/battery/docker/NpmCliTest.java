
package com.synopsys.integration.detect.battery.docker;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import com.synopsys.integration.exception.IntegrationException;

public class NpmCliTest {
    
    public static String PROJECT_NAME = "npm10";
    
    /**
     * This test will grab the latest npm10 and hopefully alert us if there are any unexpected breaking
     * changes in a minor version.
     * @throws IntegrationException 
     * @throws IOException 
     */
    @Test
    void npmCliLatestNpm10() throws IntegrationException, IOException {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("detect-npm-cli-detector", "npm-10:1.0.0")) {
            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("Npm_10.dockerfile"));

            // Set up blackduck connection and environment
            String projectVersion = PROJECT_NAME + "-1.0.0";
            BlackDuckTestConnection blackDuckTestConnection = BlackDuckTestConnection.fromEnvironment();
            BlackDuckAssertions blackduckAssertions = blackDuckTestConnection.projectVersionAssertions(PROJECT_NAME, projectVersion);
            blackduckAssertions.emptyOnBlackDuck();

            // Build command with BlackDuck config
            DetectCommandBuilder commandBuilder = new DetectCommandBuilder().defaults().defaultDirectories(test);
            commandBuilder.connectToBlackDuck(blackDuckTestConnection);
            commandBuilder.projectNameVersion(blackduckAssertions);
            commandBuilder.waitForResults();

            // Set up Detect properties
            commandBuilder.tools(DetectTool.DETECTOR);
            commandBuilder.property(DetectProperties.DETECT_SOURCE_PATH, "/opt/project/src/github-action-2.2");
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            assertEquals("", dockerAssertions.returnLogs());
            
            // Detect specific assertions
            dockerAssertions.logContains("NPM CLI: SUCCESS");
            dockerAssertions.logContains("NPM: SUCCESS");
            dockerAssertions.atLeastOneBdioFile();

            blackduckAssertions.checkComponentVersionExists("@actions/core", "1.2.6");
            blackduckAssertions.checkComponentVersionExists("shelljs", "0.8.3");
        }
    }
}
