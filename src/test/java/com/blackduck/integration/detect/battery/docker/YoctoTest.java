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
import com.blackduck.integration.detector.base.DetectorType;

@Tag("integration")
public class YoctoTest {
    @Test
    void smokeTest() throws IOException {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("detect-yocto-smoke", "detect-yocto-smoke:5.0.2")) {
            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("Yocto.dockerfile"));

            DetectCommandBuilder commandBuilder = DetectCommandBuilder.withOfflineDefaults().defaultDirectories(test);
            commandBuilder.tools(DetectTool.DETECTOR);
            commandBuilder.property(DetectProperties.DETECT_BITBAKE_PACKAGE_NAMES, "core-image-minimal");

            DockerAssertions dockerAssertions = test.run(commandBuilder);

            dockerAssertions.logContains("Bitbake CLI: SUCCESS");
            dockerAssertions.successfulDetectorType(DetectorType.BITBAKE.toString());
            dockerAssertions.atLeastOneBdioFile();
        }
    }
}