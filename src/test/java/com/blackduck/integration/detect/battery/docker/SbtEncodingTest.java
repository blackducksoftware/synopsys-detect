package com.blackduck.integration.detect.battery.docker;

import java.io.IOException;

import com.blackduck.integration.detect.configuration.DetectProperties;
import com.blackduck.integration.detect.configuration.enumeration.DetectTool;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.blackduck.integration.detect.battery.docker.provider.BuildDockerImageProvider;
import com.blackduck.integration.detect.battery.docker.util.DetectCommandBuilder;
import com.blackduck.integration.detect.battery.docker.util.DetectDockerTestRunner;
import com.blackduck.integration.detect.battery.docker.util.DockerAssertions;

@Tag("integration")
public class SbtEncodingTest {
    @Test
    void sbtEncoding() throws IOException {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("detect-sbt-encoding", "detect-sbt-encoding:1.0.3")) {
            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("SbtEncoding.dockerfile"));

            DetectCommandBuilder commandBuilder = DetectCommandBuilder.withOfflineDefaults().defaultDirectories(test);
            commandBuilder.tools(DetectTool.DETECTOR);
            commandBuilder.property(DetectProperties.DETECT_SBT_ARGUMENTS, "-Dsbt.log.noformat=true");
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            dockerAssertions.atLeastOneBdioFile();
            dockerAssertions.projectVersion("sbt-simple-project_2.12", "1.0.0-SNAPSHOT");
        }
    }
}
