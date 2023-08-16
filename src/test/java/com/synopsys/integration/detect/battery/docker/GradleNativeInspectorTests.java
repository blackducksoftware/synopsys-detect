package com.synopsys.integration.detect.battery.docker;

import com.synopsys.integration.detect.battery.docker.provider.BuildDockerImageProvider;
import com.synopsys.integration.detect.battery.docker.util.DetectCommandBuilder;
import com.synopsys.integration.detect.battery.docker.util.DetectDockerTestRunner;
import com.synopsys.integration.detect.battery.docker.util.DockerAssertions;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detector.base.DetectorType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Tag("integration")
public class GradleNativeInspectorTests {
    
    @Test
    void gradleInspector_7_6_fixed() throws IOException, InterruptedException {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("detect-gradle-native-inspector", "gradle-simple:1.0.0")) {
            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("SimpleGradle_7_6.dockerfile"));

            DetectCommandBuilder commandBuilder = DetectCommandBuilder.withOfflineDefaults().defaultDirectories(test);
            commandBuilder.property(DetectProperties.DETECT_TOOLS, "DETECTOR");
            commandBuilder.property(DetectProperties.BLACKDUCK_OFFLINE_MODE, "true");
            commandBuilder.property(DetectProperties.DETECT_ACCURACY_REQUIRED, "NONE");
            commandBuilder.property(DetectProperties.DETECT_INCLUDED_DETECTOR_TYPES, DetectorType.GRADLE.toString());
            commandBuilder.property(DetectProperties.DETECT_GRADLE_PATH, "/tmp"); // force cli failure
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            dockerAssertions.logContains("Gradle Native Inspector: SUCCESS");
            dockerAssertions.logContains("GRADLE: SUCCESS");
            dockerAssertions.atLeastOneBdioFile();
        }
    }
    
    @Test
    void gradleInspector_7_6() throws IOException, InterruptedException {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("detect-gradle-native-inspector", "gradle-simple-7-6:1.0.0")) {
            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("SimpleGradle_7_6.dockerfile"));

            DetectCommandBuilder commandBuilder = DetectCommandBuilder.withOfflineDefaults().defaultDirectories(test);
            commandBuilder.property(DetectProperties.DETECT_TOOLS, "DETECTOR");
            commandBuilder.property(DetectProperties.BLACKDUCK_OFFLINE_MODE, "true");
            commandBuilder.property(DetectProperties.DETECT_ACCURACY_REQUIRED, "NONE");
            commandBuilder.property(DetectProperties.DETECT_INCLUDED_DETECTOR_TYPES, DetectorType.GRADLE.toString());
            commandBuilder.property(DetectProperties.DETECT_GRADLE_PATH, "/tmp"); // force cli failure
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            dockerAssertions.logContains("Gradle Native Inspector: SUCCESS");
            dockerAssertions.logContains("GRADLE: SUCCESS");
            dockerAssertions.atLeastOneBdioFile();
        }
    }

    @Test
    void gradleInspector_8_2() throws IOException, InterruptedException {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("detect-gradle-native-inspector", "gradle-simple-8-2:1.0.0")) {
            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("SimpleGradle_8_2.dockerfile"));

            DetectCommandBuilder commandBuilder = DetectCommandBuilder.withOfflineDefaults().defaultDirectories(test);
            commandBuilder.property(DetectProperties.DETECT_TOOLS, "DETECTOR");
            commandBuilder.property(DetectProperties.BLACKDUCK_OFFLINE_MODE, "true");
            commandBuilder.property(DetectProperties.DETECT_ACCURACY_REQUIRED, "NONE");
            commandBuilder.property(DetectProperties.DETECT_INCLUDED_DETECTOR_TYPES, DetectorType.GRADLE.toString());
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            dockerAssertions.logContains("Gradle Native Inspector: SUCCESS");
            dockerAssertions.logContains("GRADLE: SUCCESS");
            dockerAssertions.atLeastOneBdioFile();
        }
    }
}
