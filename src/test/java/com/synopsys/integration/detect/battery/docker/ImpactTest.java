package com.synopsys.integration.detect.battery.docker;

import java.io.IOException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.docker.provider.BuildDockerImageProvider;
import com.synopsys.integration.detect.battery.docker.util.DetectCommandBuilder;
import com.synopsys.integration.detect.battery.docker.util.DetectDockerTest;
import com.synopsys.integration.detect.battery.util.DockerTestAssertions;
import com.synopsys.integration.detect.configuration.DetectProperties;

@Tag("docker")
public class ImpactTest {
    @Test
    void offlineImpact() throws IOException, InterruptedException {
        DetectDockerTest test = new DetectDockerTest("detect-impact-test", "detect-impact-test:1.0.0");
        test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("Impact.dockerfile"));

        DetectCommandBuilder commandBuilder = new DetectCommandBuilder();
        commandBuilder.property(DetectProperties.DETECT_TOOLS, "IMPACT_ANALYSIS");
        commandBuilder.property(DetectProperties.BLACKDUCK_OFFLINE_MODE, "true");
        commandBuilder.property(DetectProperties.DETECT_CLEANUP, "false");
        commandBuilder.property(DetectProperties.LOGGING_LEVEL_COM_SYNOPSYS_INTEGRATION, "DEBUG");
        commandBuilder.property(DetectProperties.DETECT_IMPACT_ANALYSIS_ENABLED, "true");
        DockerTestAssertions result = test.run(commandBuilder);

        result.successfulTool("IMPACT_ANALYSIS");
        result.logContainsPattern("Vulnerability Impact Analysis generated report at /opt/results/output/runs/", "/impact-analysis/external-method-uses.bdmu");
        result.successfulOperation("Generate Impact Analysis File");
    }
}
