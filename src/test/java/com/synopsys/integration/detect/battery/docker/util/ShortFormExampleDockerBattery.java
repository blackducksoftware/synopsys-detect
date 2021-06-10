package com.synopsys.integration.detect.battery.docker.util;

import java.io.IOException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.docker.provider.BuildDockerImageProvider;
import com.synopsys.integration.detect.battery.util.DockerTestAssertions;
import com.synopsys.integration.detect.configuration.DetectProperties;

@Tag("docker")
public class ShortFormExampleDockerBattery {
    @Test
    void run() throws IOException, InterruptedException {
        DetectDockerTest test = new DetectDockerTest("short-form-test", "detect-docker-yarn-berry:1.0.0");
        test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("Yarn_Berry.dockerfile"));

        DetectCommandBuilder commandBuilder = new DetectCommandBuilder();
        commandBuilder.property(DetectProperties.DETECT_TOOLS, "DETECTOR");
        commandBuilder.property(DetectProperties.BLACKDUCK_OFFLINE_MODE, "true");
        commandBuilder.property(DetectProperties.DETECT_CLEANUP, "false");
        commandBuilder.property(DetectProperties.LOGGING_LEVEL_COM_SYNOPSYS_INTEGRATION, "INFO");
        DockerTestAssertions result = test.run(commandBuilder);

        result.atLeastOneBdioFile();
        result.successfulDetectorType("DETECT");
    }

}
