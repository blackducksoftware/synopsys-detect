package com.synopsys.integration.detect.battery.docker;

import java.io.IOException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.docker.provider.BuildDockerImageProvider;
import com.synopsys.integration.detect.battery.docker.util.DetectCommandBuilder;
import com.synopsys.integration.detect.battery.docker.util.DetectDockerTestBuilder;
import com.synopsys.integration.detect.battery.util.DockerTestAssertions;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;

@Tag("docker")
public class SbtEncodingTest {
    @Test
    void sbtEncoding() throws IOException, InterruptedException {
        DetectDockerTestBuilder test = new DetectDockerTestBuilder("detect-sbt-encoding", "detect-sbt-encoding:1.0.3");
        test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("SbtEncoding.dockerfile"));

        DetectCommandBuilder commandBuilder = DetectCommandBuilder.withDefaults();
        commandBuilder.tools(DetectTool.DETECTOR);
        commandBuilder.property(DetectProperties.DETECT_SBT_ARGUMENTS, "-Dsbt.log.noformat=true");
        DockerTestAssertions result = test.run(commandBuilder);

        result.atLeastOneBdioFile();
        result.projectVersion("sbt-simple-project_2.12", "1.0.0-SNAPSHOT");
    }
}
