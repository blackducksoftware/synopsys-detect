package com.synopsys.integration.detect.battery.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.BatteryTest;
import com.synopsys.integration.detect.configuration.DetectProperties;

@Tag("battery")
public class BazelBattery {
    private static final String BAZEL_MAVEN_INSTALL_OUTPUT_RESOURCE = "bazel-maven-install-query.xout";

    @Test
    void bazelMavenInstall() {
        BatteryTest test = new BatteryTest("bazel-maven-install", "bazel");
        test.withToolsValue("BAZEL");
        test.property("detect.bazel.target", "//://tests/integration:ArtifactExclusionsTest");
        test.property("detect.bazel.dependency.type", "MAVEN_INSTALL");
        test.executableFromResourceFiles(DetectProperties.Companion.getDETECT_BAZEL_PATH(), BAZEL_MAVEN_INSTALL_OUTPUT_RESOURCE);
        test.sourceDirectoryNamed("bazel-maven-install");
        test.sourceFileNamed("WORKSPACE");
        test.expectBdioResources();
        test.run();
    }
}
