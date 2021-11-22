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
public class Dotnet5Test {
    @Test
    void detectUsesDotnet5() throws IOException, InterruptedException {
        DetectDockerTestRunner test = new DetectDockerTestRunner("detect-dotnet-five", "detect-dotnet-five:1.0.1");
        test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("Dotnet5.dockerfile"));

        DetectCommandBuilder commandBuilder = DetectCommandBuilder.withOfflineDefaults().defaultDirectories(test);
        commandBuilder.property(DetectProperties.DETECT_TOOLS, "DETECTOR");
        commandBuilder.property(DetectProperties.BLACKDUCK_OFFLINE_MODE, "true");
        DockerAssertions dockerAssertions = test.run(commandBuilder);

        dockerAssertions.successfulDetectorType("NUGET");
        dockerAssertions.atLeastOneBdioFile();
        dockerAssertions.logContains("https://sig-repo.synopsys.com/artifactory/bds-integrations-nuget-release/"); // Verify we are using the EXTERNAL artifactory to download the inspector.

        test.cleanupDirs();
    }

}
