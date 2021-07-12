package com.synopsys.integration.detect.battery.docker.integration;

import java.io.IOException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.docker.provider.BuildDockerImageProvider;
import com.synopsys.integration.detect.battery.docker.util.DetectCommandBuilder;
import com.synopsys.integration.detect.battery.docker.util.DetectDockerTestBuilder;
import com.synopsys.integration.detect.battery.util.DockerAssertions;
import com.synopsys.integration.exception.IntegrationException;

@Tag("integration")
public class DetectOnDetectTest {
    @Test
    void detectOnDetect() throws IOException, InterruptedException, IntegrationException {
        DetectDockerTestBuilder test = new DetectDockerTestBuilder("detect-on-detect", "detect-7.1.0:1.0.0");
        test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("Detect-7.1.0.dockerfile"));

        BlackDuckTestConnection blackDuckTestConnection = BlackDuckTestConnection.fromEnvironment();
        BlackDuckAssertions blackduckAssertions = blackDuckTestConnection.projectVersionAssertions("detect-on-detect-docker", "happy-path");
        blackduckAssertions.emptyOnBlackDuck();

        DetectCommandBuilder commandBuilder = new DetectCommandBuilder().defaults();
        commandBuilder.connectToBlackDuck(blackDuckTestConnection);
        commandBuilder.projectNameVersion(blackduckAssertions);
        commandBuilder.waitForResults();

        DockerAssertions dockerAssertions = test.run(commandBuilder);

        dockerAssertions.bdioFiles(6); //7 code locations, 6 bdio, 1 signature scanner

        blackduckAssertions.hasCodeLocations("src/detect-on-detect-docker/happy-path scan",
            "detect-on-detect-docker/happy-path/detectable/com.synopsys.integration/detectable/7.1.1-SNAPSHOT gradle/bom",
            "detect-on-detect-docker/happy-path/com.synopsys.integration/synopsys-detect/7.1.1-SNAPSHOT gradle/bom",
            "detect-on-detect-docker/happy-path/common/com.synopsys.integration/common/7.1.1-SNAPSHOT gradle/bom",
            "detect-on-detect-docker/happy-path/common-test/com.synopsys.integration/common-test/7.1.1-SNAPSHOT gradle/bom",
            "detect-on-detect-docker/happy-path/configuration/com.synopsys.integration/configuration/7.1.1-SNAPSHOT gradle/bom",
            "detect-on-detect-docker/happy-path/detector/com.synopsys.integration/detector/7.1.1-SNAPSHOT gradle/bom");

        blackduckAssertions.hasComponents("jackson-core");
    }

}
