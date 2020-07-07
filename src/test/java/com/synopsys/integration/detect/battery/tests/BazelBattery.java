package com.synopsys.integration.detect.battery.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.BatteryTest;
import com.synopsys.integration.detect.configuration.DetectProperties;

@Tag("battery")
public class BazelBattery {
    private static final String BAZEL_MAVEN_INSTALL_OUTPUT_RESOURCE = "bazel-maven-install-query.xout";
    private static final String BAZEL_HASKELL_CABAL_LIBRARY_OUTPUT_RESOURCE = "bazel-haskell-cabal-library-query.xout";

    @Test
    void bazelMavenInstall() {
        BatteryTest test = new BatteryTest("bazel-maven-install", "bazel/maven-install");
        test.withToolsValue("BAZEL");
        test.property("detect.bazel.target", "//tests/integration:ArtifactExclusionsTest");
        test.property("detect.bazel.dependency.type", "MAVEN_INSTALL");
        test.executableFromResourceFiles(DetectProperties.Companion.getDETECT_BAZEL_PATH(), BAZEL_MAVEN_INSTALL_OUTPUT_RESOURCE);
        test.sourceDirectoryNamed("bazel-maven-install");
        test.sourceFileNamed("WORKSPACE");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void bazelHaskellCabalLibrary() {
        BatteryTest test = new BatteryTest("bazel-haskell-cabal-library", "bazel/haskell-cabal-library");
        test.withToolsValue("BAZEL");
        test.property("detect.bazel.target", "//cat_hs/lib/args:args");
        test.property("detect.bazel.dependency.type", "HASKELL_CABAL_LIBRARY");
        test.executableFromResourceFiles(DetectProperties.Companion.getDETECT_BAZEL_PATH(), BAZEL_HASKELL_CABAL_LIBRARY_OUTPUT_RESOURCE);
        test.sourceDirectoryNamed("bazel-haskell-cabal-library");
        test.sourceFileNamed("WORKSPACE");
        test.expectBdioResources();
        test.run();
    }
}
