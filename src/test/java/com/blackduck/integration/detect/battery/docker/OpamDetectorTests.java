package com.blackduck.integration.detect.battery.docker;

import com.blackduck.integration.detect.battery.docker.integration.BlackDuckAssertions;
import com.blackduck.integration.detect.battery.docker.integration.BlackDuckTestConnection;
import com.blackduck.integration.detect.battery.docker.provider.BuildDockerImageProvider;
import com.blackduck.integration.detect.battery.docker.util.DetectCommandBuilder;
import com.blackduck.integration.detect.battery.docker.util.DetectDockerTestRunner;
import com.blackduck.integration.detect.battery.docker.util.DockerAssertions;
import com.blackduck.integration.detect.configuration.DetectProperties;
import com.blackduck.integration.detect.configuration.enumeration.DetectTool;
import com.blackduck.integration.detector.base.DetectorType;
import com.blackduck.integration.exception.IntegrationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class OpamDetectorTests {

    @Test
    void opamLockFileDetectorTest() throws IOException, IntegrationException {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("opam-lockfile-detector", "opam-lockfile-detector:1.0.0")) {
            BuildDockerImageProvider buildDockerImageProvider = BuildDockerImageProvider.forDockerfilResourceNamed("OpamLockFileTest.dockerfile");
            test.withImageProvider(buildDockerImageProvider);

            // Set up blackduck connection and environment
            String projectVersion = "opam-lockfile-detector-1.0.0";
            BlackDuckTestConnection blackDuckTestConnection = BlackDuckTestConnection.fromEnvironment();
            BlackDuckAssertions blackduckAssertions = blackDuckTestConnection.projectVersionAssertions("opam-lockfile-detector", projectVersion);
            blackduckAssertions.emptyOnBlackDuck();

            // Build command with BlackDuck config
            DetectCommandBuilder commandBuilder = new DetectCommandBuilder().defaults().defaultDirectories(test);
            commandBuilder.connectToBlackDuck(blackDuckTestConnection);
            commandBuilder.projectNameVersion(blackduckAssertions);
            commandBuilder.waitForResults();

            // Set up Detect properties
            commandBuilder.property(DetectProperties.DETECT_TOOLS, DetectTool.DETECTOR.toString());
            commandBuilder.property(DetectProperties.DETECT_INCLUDED_DETECTOR_TYPES, DetectorType.OPAM.toString());
            commandBuilder.property(DetectProperties.DETECT_ACCURACY_REQUIRED, "NONE");
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            // Detect specific assertions
            dockerAssertions.logContains("Opam Lock File: SUCCESS");
            dockerAssertions.logContains("Opam CLI: ATTEMPTED");
            dockerAssertions.atLeastOneBdioFile();

            blackduckAssertions.checkComponentVersionExists("sexplib", "v0.15.1");
            blackduckAssertions.checkComponentVersionExists("ocaml-version", "3.5.0");
        }
    }

    @Test
    void opamShowDetectorTest() throws IOException, IntegrationException {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("opam-show-detector", "opam-show-detector:1.0.0")) {
            BuildDockerImageProvider buildDockerImageProvider = BuildDockerImageProvider.forDockerfilResourceNamed("OpamShowTest.dockerfile");
            test.withImageProvider(buildDockerImageProvider);

            // Set up blackduck connection and environment
            String projectVersion = "opam-show-detector-1.0.0";
            BlackDuckTestConnection blackDuckTestConnection = BlackDuckTestConnection.fromEnvironment();
            BlackDuckAssertions blackduckAssertions = blackDuckTestConnection.projectVersionAssertions("opam-show-detector", projectVersion);
            blackduckAssertions.emptyOnBlackDuck();

            // Build command with BlackDuck config
            DetectCommandBuilder commandBuilder = new DetectCommandBuilder().defaults().defaultDirectories(test);
            commandBuilder.connectToBlackDuck(blackDuckTestConnection);
            commandBuilder.projectNameVersion(blackduckAssertions);
            commandBuilder.waitForResults();

            // Set up Detect properties
            commandBuilder.property(DetectProperties.DETECT_TOOLS, DetectTool.DETECTOR.toString());
            commandBuilder.property(DetectProperties.DETECT_INCLUDED_DETECTOR_TYPES, DetectorType.OPAM.toString());
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            // Detect specific assertions
            dockerAssertions.logContains("Opam CLI: SUCCESS");
            dockerAssertions.atLeastOneBdioFile();

            blackduckAssertions.checkComponentVersionExists("menhirCST", "20240715");
            blackduckAssertions.checkComponentVersionExists("flexdll", "0.43");
            blackduckAssertions.checkComponentVersionExists("ptime", "1.2.0");
        }
    }

    @Test
    void opamTreeDetectorTest() throws IntegrationException, IOException {
        try (DetectDockerTestRunner test = new DetectDockerTestRunner("opam-tree-detector", "opam-tree-detector:1.0.0")) {

            BuildDockerImageProvider buildDockerImageProvider = BuildDockerImageProvider.forDockerfilResourceNamed("OpamTreeTest.dockerfile");
            test.withImageProvider(buildDockerImageProvider);

            // Set up blackduck connection and environment
            String projectVersion = "opam-tree-detector-1.0.0";
            BlackDuckTestConnection blackDuckTestConnection = BlackDuckTestConnection.fromEnvironment();
            BlackDuckAssertions blackduckAssertions = blackDuckTestConnection.projectVersionAssertions("opam-tree-detector", projectVersion);
            blackduckAssertions.emptyOnBlackDuck();

            // Build command with BlackDuck config
            DetectCommandBuilder commandBuilder = new DetectCommandBuilder().defaults().defaultDirectories(test);
            commandBuilder.connectToBlackDuck(blackDuckTestConnection);
            commandBuilder.projectNameVersion(blackduckAssertions);
            commandBuilder.waitForResults();

            // Set up Detect properties
            commandBuilder.property(DetectProperties.DETECT_TOOLS, DetectTool.DETECTOR.toString());
            commandBuilder.property(DetectProperties.DETECT_INCLUDED_DETECTOR_TYPES, DetectorType.OPAM.toString());
            DockerAssertions dockerAssertions = test.run(commandBuilder);

            // Detect specific assertions
            dockerAssertions.logContains("Opam CLI: SUCCESS");
            dockerAssertions.atLeastOneBdioFile();

            blackduckAssertions.checkComponentVersionExists("mlx", "0.9");
            blackduckAssertions.checkComponentVersionExists("yojson", "2.2.2");
            blackduckAssertions.checkComponentVersionExists("emile", "1.1");
            blackduckAssertions.checkComponentVersionExists("result","1.5");
        }
    }

}
