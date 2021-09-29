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
import com.synopsys.integration.util.NameVersion;

@Tag("integration")
public class ProjectInspectorJunitTests {
    @Test
    void mavenProjectInspectorLegacyIsTheDefault() throws IOException, InterruptedException {
        DetectDockerTestRunner test = new DetectDockerTestRunner("detect-maven-project-inspector-junit", "maven-junit:1.0.0");
        test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("MavenJunitBuildless.dockerfile"));

        DetectCommandBuilder commandBuilder = new DetectCommandBuilder().defaults().defaultDirectories(test);
        commandBuilder.property(DetectProperties.DETECT_TOOLS, "DETECTOR");
        commandBuilder.property(DetectProperties.DETECT_BUILDLESS, "true");
        commandBuilder.property(DetectProperties.DETECT_INCLUDED_DETECTOR_TYPES, DetectorType.MAVEN.toString());
        commandBuilder.property(DetectProperties.DETECT_MAVEN_BUILDLESS_LEGACY_MODE, "false");

        commandBuilder.property(DetectProperties.BLACKDUCK_URL, "https://us1a-int-hub02.nprd.sig.synopsys.com/");
        commandBuilder.property(DetectProperties.BLACKDUCK_API_TOKEN, "YzQyNTBhYjktNmMxZi00MzEyLThhYTAtMjU2M2Q1YTZhYzFjOjYzM2NlYzhkLWRmYzAtNDU2OC05NGZlLTUyNGQ4NWVlNzBhZA==");
        commandBuilder.property(DetectProperties.BLACKDUCK_TRUST_CERT, "true");

        commandBuilder.projectNameVersion(new NameVersion("maven buildless junit", "1.0.0"));

        DockerAssertions dockerAssertions = test.run(commandBuilder);

        dockerAssertions.successfulDetectorType("MAVEN");
        dockerAssertions.atLeastOneBdioFile();
        dockerAssertions.logDoesNotContain("Maven Project Inspector");
        dockerAssertions.logContains("Maven Pom Parse");
    }
}
