package com.synopsys.integration.detect.battery.docker;

import java.io.IOException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.docker.provider.BuildDockerImageProvider;
import com.synopsys.integration.detect.battery.docker.util.DetectCommandBuilder;
import com.synopsys.integration.detect.battery.docker.util.DetectDockerTestRunner;
import com.synopsys.integration.detect.battery.docker.util.DockerAssertions;
import com.synopsys.integration.detect.configuration.DetectProperties;

//@Tag("integration")
public class GoModTest {
    @Test
    void goModExecutablesTest() throws IOException {
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
}
