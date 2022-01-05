package com.synopsys.integration.detect.battery.detector;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.util.DetectorBatteryTestRunner;
import com.synopsys.integration.detect.configuration.DetectProperties;

@Tag("battery")
public class BitbakeBattery {

    @Test
    void testIncludeAll() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("bitbake-full", "bitbake/full");
        test.sourceFileFromResource("oe-init-build-env");
        test.sourceFileFromResource("task-depends.dot");
        test.executableFromResourceFiles(DetectProperties.DETECT_BASH_PATH.getProperty(), "bitbake-g.xout", "bitbake-layers-show-recipes.xout");
        test.property("detect.bitbake.package.names", "core-image-sato");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void testExclDev() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("bitbake-excldev", "bitbake/excldev");
        test.sourceFileFromResource("oe-init-build-env");
        test.sourceFileFromResource("task-depends.dot");
        test.sourceFileFromResource("license.manifest");
        test.executableFromResourceFiles(DetectProperties.DETECT_BASH_PATH.getProperty(), "bitbake-g.xout", "bitbake-layers-show-recipes.xout");
        test.property("detect.bitbake.package.names", "core-image-sato");
        test.property("detect.bitbake.include.dev.dependencies", "false");
        test.expectBdioResources();
        test.run();
    }
}
