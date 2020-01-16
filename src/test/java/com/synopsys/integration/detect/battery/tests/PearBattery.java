package com.synopsys.integration.detect.battery.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.BatteryTest;
import com.synopsys.integration.detect.configuration.DetectProperties;

@Tag("battery")
public class PearBattery {
    @Test
    void lock() {
        final BatteryTest test = new BatteryTest("pear-cli");
        test.sourceDirectoryNamed("linux-pear");
        test.sourceFileFromResource("package.xml");
        test.executableFromResourceFiles(DetectProperties.Companion.getDETECT_PEAR_PATH(), "pear-list.xout", "pear-package.xout");
        test.expectBdioResources();
        test.run();
    }
}

