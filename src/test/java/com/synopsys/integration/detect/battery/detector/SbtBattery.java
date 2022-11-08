package com.synopsys.integration.detect.battery.detector;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.util.DetectorBatteryTestRunner;
import com.synopsys.integration.detect.configuration.DetectProperties;

@Tag("battery")
public class SbtBattery {
    @Test
    void dotPlugin() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("sbt-dot");
        test.sourceDirectoryNamed("sbt-dot");
        test.sourceFileNamed("build.sbt");
        test.addDirectlyToSourceFolderFromExpandedResource("dots");
        test.executableFromResourceFiles(DetectProperties.DETECT_SBT_PATH, "sbt-plugins.xout", "sbt-dependencyDot.ftl");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void dotPluginMultipleProjectNode() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("sbt-dot-multipleprojectnode");
        test.sourceDirectoryNamed("sbt-dot");
        test.sourceFileNamed("build.sbt");
        test.addDirectlyToSourceFolderFromExpandedResource("dots");
        test.executableFromResourceFiles(DetectProperties.DETECT_SBT_PATH, "sbt-plugins.xout", "sbt-dependencyDot.ftl");
        test.expectBdioResources();
        test.run();
    }
}

