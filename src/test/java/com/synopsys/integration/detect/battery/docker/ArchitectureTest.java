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
public class ArchitectureTest {
    @Test
    void linuxHasArchitectureLog() throws IOException {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("detect-architecture", "empty-linux:1.0.0")) {

            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("EmptyLinux.dockerfile"));
            DetectCommandBuilder commandBuilder = DetectCommandBuilder.withOfflineDefaults().defaultDirectories(test);
            commandBuilder.property(DetectProperties.DETECT_TOOLS, "DETECTOR");
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            dockerAssertions.logContains("You seem to be using amd64 architecture.");
        }
    }
}
