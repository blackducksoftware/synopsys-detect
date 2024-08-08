package com.synopsys.integration.detect.battery.docker;

import java.io.IOException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.docker.provider.BuildDockerImageProvider;
import com.synopsys.integration.detect.battery.docker.util.DetectCommandBuilder;
import com.synopsys.integration.detect.battery.docker.util.DetectDockerTestRunner;
import com.synopsys.integration.detect.battery.docker.util.DockerAssertions;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detector.base.DetectorType;

@Tag("integration")
public class DartTest {
    @Test
    void smokeTest() throws IOException {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("detect-dart-smoke", "detect-dart-smoke:3.5.0-1")) {
            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("Dart.dockerfile"));

            DetectCommandBuilder commandBuilder = DetectCommandBuilder.withOfflineDefaults().defaultDirectories(test);
            commandBuilder.tools(DetectTool.DETECTOR);

            DockerAssertions dockerAssertions = test.run(commandBuilder);

            dockerAssertions.logContains("Dart CLI: SUCCESS");
            dockerAssertions.successfulDetectorType(DetectorType.DART.toString());
            dockerAssertions.atLeastOneBdioFile();
        }
    }
}
