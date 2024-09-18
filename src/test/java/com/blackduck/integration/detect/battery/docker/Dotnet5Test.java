package com.blackduck.integration.detect.battery.docker;

import java.io.IOException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.blackduck.integration.detect.battery.docker.provider.BuildDockerImageProvider;
import com.blackduck.integration.detect.battery.docker.util.DetectCommandBuilder;
import com.blackduck.integration.detect.battery.docker.util.DetectDockerTestRunner;
import com.blackduck.integration.detect.battery.docker.util.DockerAssertions;
import com.blackduck.integration.detect.configuration.DetectProperties;

@Tag("integration")
public class Dotnet5Test {
    @Test
    void detectUsesDotnet5() throws IOException {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("detect-dotnet-five", "detect-dotnet-five:1.0.1")) {
            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("Dotnet5.dockerfile"));

            DetectCommandBuilder commandBuilder = DetectCommandBuilder.withOfflineDefaults().defaultDirectories(test);
            commandBuilder.property(DetectProperties.DETECT_TOOLS, "DETECTOR");
            commandBuilder.property(DetectProperties.BLACKDUCK_OFFLINE_MODE, "true");
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            dockerAssertions.successfulDetectorType("NUGET");
            dockerAssertions.atLeastOneBdioFile();
            dockerAssertions.logContainsPattern("https://repo.blackduck.com/.*bds-integrations-release/com/blackduck/integration/detect-nuget-inspector/"); // Verify we are using the EXTERNAL artifactory to download the inspector.
        }
    }

}
