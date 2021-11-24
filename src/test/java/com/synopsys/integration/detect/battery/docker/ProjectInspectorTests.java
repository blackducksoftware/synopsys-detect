package com.synopsys.integration.detect.battery.docker;

import java.io.IOException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.docker.provider.BuildDockerImageProvider;
import com.synopsys.integration.detect.battery.docker.util.DetectCommandBuilder;
import com.synopsys.integration.detect.battery.docker.util.DetectDockerTestRunner;
import com.synopsys.integration.detect.battery.docker.util.DockerAssertions;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detector.base.DetectorType;

@Tag("integration")
public class ProjectInspectorTests {
    @Test
    void dotnetProjectInspector() throws IOException {
        // DetectDockerTestRunner must be declared in try-with-resources block to take advantage of "close" method (cleans up resources)
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("detect-dotnet-project-inspector", "detect-dotnet-five:1.0.1")) {
            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("Dotnet5.dockerfile"));

            DetectCommandBuilder commandBuilder = DetectCommandBuilder.withOfflineDefaults().defaultDirectories(test);
            commandBuilder.property(DetectProperties.DETECT_TOOLS, "DETECTOR");
            commandBuilder.property(DetectProperties.BLACKDUCK_OFFLINE_MODE, "true");
            commandBuilder.property(DetectProperties.DETECT_BUILDLESS, "true");
            commandBuilder.property(DetectProperties.DETECT_INCLUDED_DETECTOR_TYPES, DetectorType.NUGET.toString());
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            dockerAssertions.successfulDetectorType("NUGET");
            dockerAssertions.atLeastOneBdioFile();
            dockerAssertions.logContains("NuGet Project Inspector");
        }
    }

    @Test
    void gradleProjectInspector() throws IOException, InterruptedException {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("detect-gradle-project-inspector", "gradle-simple:1.0.0")) {
            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("SimpleGradle.dockerfile"));

            DetectCommandBuilder commandBuilder = DetectCommandBuilder.withOfflineDefaults().defaultDirectories(test);
            commandBuilder.property(DetectProperties.DETECT_TOOLS, "DETECTOR");
            commandBuilder.property(DetectProperties.BLACKDUCK_OFFLINE_MODE, "true");
            commandBuilder.property(DetectProperties.DETECT_BUILDLESS, "true");
            commandBuilder.property(DetectProperties.DETECT_INCLUDED_DETECTOR_TYPES, DetectorType.GRADLE.toString());
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            dockerAssertions.successfulDetectorType("GRADLE");
            dockerAssertions.atLeastOneBdioFile();
            dockerAssertions.logContains("Gradle Project Inspector");
        }
    }

    @Test
    void mavenProjectInspectorLegacyIsTheDefault() throws IOException, InterruptedException {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("detect-maven-project-inspector-legacy", "maven-simple:1.0.0")) {
            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("SimpleMaven.dockerfile"));

            DetectCommandBuilder commandBuilder = DetectCommandBuilder.withOfflineDefaults().defaultDirectories(test);
            commandBuilder.property(DetectProperties.DETECT_TOOLS, "DETECTOR");
            commandBuilder.property(DetectProperties.BLACKDUCK_OFFLINE_MODE, "true");
            commandBuilder.property(DetectProperties.DETECT_BUILDLESS, "true");
            commandBuilder.property(DetectProperties.DETECT_INCLUDED_DETECTOR_TYPES, DetectorType.MAVEN.toString());
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            dockerAssertions.successfulDetectorType("MAVEN");
            dockerAssertions.atLeastOneBdioFile();
            dockerAssertions.logDoesNotContain("Maven Project Inspector");
            dockerAssertions.logContains("Maven Pom Parse");
        }
    }

    @Test
    void mavenProjectInspector() throws IOException, InterruptedException {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("detect-maven-project-inspector", "maven-simple:1.0.0")) {
            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("SimpleMaven.dockerfile"));

            DetectCommandBuilder commandBuilder = DetectCommandBuilder.withOfflineDefaults().defaultDirectories(test);
            commandBuilder.property(DetectProperties.DETECT_TOOLS, "DETECTOR");
            commandBuilder.property(DetectProperties.BLACKDUCK_OFFLINE_MODE, "true");
            commandBuilder.property(DetectProperties.DETECT_BUILDLESS, "true");
            commandBuilder.property(DetectProperties.DETECT_INCLUDED_DETECTOR_TYPES, DetectorType.MAVEN.toString());
            commandBuilder.property(DetectProperties.DETECT_MAVEN_BUILDLESS_LEGACY_MODE, "false");
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            dockerAssertions.successfulDetectorType("MAVEN");
            dockerAssertions.atLeastOneBdioFile();
            dockerAssertions.logContains("Maven Project Inspector");
        }
    }
}
