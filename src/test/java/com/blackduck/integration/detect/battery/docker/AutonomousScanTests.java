package com.blackduck.integration.detect.battery.docker;

import com.blackduck.integration.detect.battery.docker.integration.BlackDuckAssertions;
import com.blackduck.integration.detect.battery.docker.integration.BlackDuckTestConnection;
import com.blackduck.integration.detect.battery.docker.provider.BuildDockerImageProvider;
import com.blackduck.integration.detect.battery.docker.util.DetectCommandBuilder;
import com.blackduck.integration.detect.battery.docker.util.DetectDockerTestRunner;
import com.blackduck.integration.detect.battery.docker.util.DockerAssertions;
import com.blackduck.integration.detect.configuration.DetectProperties;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Tag("integration")
public class AutonomousScanTests {

    public static String ARTIFACTORY_URL = System.getenv().get("SNPS_INTERNAL_ARTIFACTORY");

//    @Test
    void autonomousScanModeOFFLINETest() throws Exception {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("autonomous-scan-mode-test-1", "detect-9.8.0:1.0.1")) {
            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("Detect-9.8.0.dockerfile"));

            DetectCommandBuilder commandBuilder = new DetectCommandBuilder().defaults().defaultDirectories(test);
            commandBuilder.waitForResults();

            String scanMode = "RAPID";

            commandBuilder.property(DetectProperties.DETECT_AUTONOMOUS_SCAN_ENABLED, String.valueOf(true));
            commandBuilder.property(DetectProperties.DETECT_BLACKDUCK_SCAN_MODE, scanMode);
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            dockerAssertions.bdioFiles(1);
            dockerAssertions.locateScanSettingsFile();
            dockerAssertions.autonomousScanModeAssertions(scanMode);
            dockerAssertions.autonomousDetectorAssertions("GRADLE","detect.gradle.configuration.types.excluded");
        }
    }

    @Test
    void autonomousScanModeONLINETest() throws Exception {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("autonomous-scan-mode-test-3", "autonomous-test:1.0.0")) {

            Map<String, String> artifactoryArgs = new HashMap<>();
            artifactoryArgs.put("artifactory_url", ARTIFACTORY_URL);

            BuildDockerImageProvider buildDockerImageProvider = BuildDockerImageProvider.forDockerfilResourceNamed("AutonomousScanTest.dockerfile");
            buildDockerImageProvider.setBuildArgs(artifactoryArgs);
            test.withImageProvider(buildDockerImageProvider);

            BlackDuckTestConnection blackDuckTestConnection = BlackDuckTestConnection.fromEnvironment();
            BlackDuckAssertions blackduckAssertions = blackDuckTestConnection.projectVersionAssertions("autonomous-scan-test", "autonomous-scan-2");
            blackduckAssertions.emptyOnBlackDuck();

            DetectCommandBuilder commandBuilder = new DetectCommandBuilder().defaults().defaultDirectories(test);
            commandBuilder.connectToBlackDuck(blackDuckTestConnection);
            commandBuilder.projectNameVersion(blackduckAssertions);
            commandBuilder.waitForResults();

            commandBuilder.property(DetectProperties.DETECT_AUTONOMOUS_SCAN_ENABLED, String.valueOf(true));
            commandBuilder.property(DetectProperties.DETECT_ACCURACY_REQUIRED, "NONE");
            commandBuilder.property(DetectProperties.DETECT_TOOLS_EXCLUDED,"BINARY_SCAN");
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            dockerAssertions.bdioFiles(1);
            dockerAssertions.locateScanSettingsFile();
            dockerAssertions.autonomousScanModeAssertions("INTELLIGENT");
            dockerAssertions.autonomousDetectorAssertions("MAVEN", "detect.maven.include.shaded.dependencies");
            dockerAssertions.autonomousDetectorAssertions("GRADLE");
            dockerAssertions.autonomousScanTypeAssertions("DETECTOR", "detect.accuracy.required", "detect.detector.search.depth");
            dockerAssertions.autonomousScanTypeAssertions("SIGNATURE_SCAN");

            blackduckAssertions.hasComponents("Apache Commons Text","XStream");
        }
    }

}
