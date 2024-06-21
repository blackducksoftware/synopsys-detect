package com.synopsys.integration.detect.battery.docker;

import com.synopsys.integration.detect.battery.docker.integration.BlackDuckAssertions;
import com.synopsys.integration.detect.battery.docker.integration.BlackDuckTestConnection;
import com.synopsys.integration.detect.battery.docker.provider.BuildDockerImageProvider;
import com.synopsys.integration.detect.battery.docker.util.DetectCommandBuilder;
import com.synopsys.integration.detect.battery.docker.util.DetectDockerTestRunner;
import com.synopsys.integration.detect.battery.docker.util.DockerAssertions;
import com.synopsys.integration.detect.configuration.DetectProperties;

import java.io.IOException;

public class AutonomousScanTests {

    void autonomousScanModeOFFLINETest() throws Exception {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("autonomous-scan-mode-test-1", "detect-9.8.0:1.0.1")) {
            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("Detect-9.8.0.dockerfile"));

            DetectCommandBuilder commandBuilder = new DetectCommandBuilder().defaults().defaultDirectories(test);
            commandBuilder.waitForResults();

            String scanMode = "RAPID";

            commandBuilder.property(DetectProperties.DETECT_AUTONOMOUS_SCAN_ENABLED, String.valueOf(true));
            commandBuilder.property(DetectProperties.DETECT_BLACKDUCK_SCAN_MODE, scanMode);
            commandBuilder.property(DetectProperties.DETECT_TOOLS,"DETECTOR");
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            dockerAssertions.bdioFiles(1);
            dockerAssertions.locateScanSettingsFile();
            dockerAssertions.logContains("Blackduck Url should be provided in order to run BINARY_SCAN or CONTAINER_SCAN in offline mode.");
            dockerAssertions.autonomousScanModeAssertions(scanMode);
            dockerAssertions.autonomousDetectorAssertions("GRADLE","detect.gradle.configuration.types.excluded");
        }
    }

    void autonomousScanModeONLINETest() throws Exception {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("autonomous-scan-mode-test-2", "detect-9.8.0:1.0.2")) {
            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("Detect-9.8.0.dockerfile"));

            BlackDuckTestConnection blackDuckTestConnection = BlackDuckTestConnection.fromEnvironment();
            BlackDuckAssertions blackduckAssertions = blackDuckTestConnection.projectVersionAssertions("autonomous-scan-test", "autonomous-scan-2");
            blackduckAssertions.emptyOnBlackDuck();

            DetectCommandBuilder commandBuilder = new DetectCommandBuilder().defaults().defaultDirectories(test);
            commandBuilder.waitForResults();

            String scanMode = "INTELLIGENT";

            commandBuilder.property(DetectProperties.DETECT_AUTONOMOUS_SCAN_ENABLED, String.valueOf(true));
            commandBuilder.property(DetectProperties.DETECT_BLACKDUCK_SCAN_MODE, scanMode);
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            dockerAssertions.bdioFiles(1);
            dockerAssertions.locateScanSettingsFile();
            dockerAssertions.autonomousScanModeAssertions(scanMode);
            dockerAssertions.autonomousDetectorAssertions("GRADLE");
            blackduckAssertions.hasCodeLocations(
                    "src/autonomous-scan-test/autonomous-scan-2 signature"
            );

            blackduckAssertions.hasComponents("jackson-core");
        }
    }

}
