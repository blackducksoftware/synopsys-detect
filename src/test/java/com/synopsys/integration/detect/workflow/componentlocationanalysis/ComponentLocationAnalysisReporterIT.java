package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.synopsys.integration.detect.battery.docker.provider.BuildDockerImageProvider;
import com.synopsys.integration.detect.battery.docker.util.DetectCommandBuilder;
import com.synopsys.integration.detect.battery.docker.util.DetectDockerTestRunner;
import com.synopsys.integration.detect.battery.docker.util.DockerAssertions;
import com.synopsys.integration.detect.configuration.DetectProperties;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Tag("integration")
public class ComponentLocationAnalysisReporterIT {
//    @Test
//    void offlinePkgMngrScan_analysisEnabled() throws IOException {
//        try (DetectDockerTestRunner test = new DetectDockerTestRunner("component-location-analysis-test", "gradle-simple:1.0.0")) {
//            test.withImageProvider(BuildDockerImageProvider.forDockerfilResourceNamed("SimpleGradle.dockerfile"));
//
//            DetectCommandBuilder commandBuilder = DetectCommandBuilder.withOfflineDefaults().defaultDirectories(test);
//            commandBuilder.property(DetectProperties.DETECT_COMPONENT_LOCATION_ANALYSIS_ENABLED, "true");
//            DockerAssertions dockerAssertions = test.run(commandBuilder);
//
//            dockerAssertions.successfulTool("IMPACT_ANALYSIS");
//            dockerAssertions.logContainsPattern("Vulnerability Impact Analysis generated report at /opt/results/output/runs/.*/impact-analysis/external-method-uses.bdmu");
//            dockerAssertions.successfulOperation("Generate Impact Analysis File");
//        }
//    }




}
