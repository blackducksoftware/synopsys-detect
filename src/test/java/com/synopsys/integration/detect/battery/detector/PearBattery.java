package com.synopsys.integration.detect.battery.detector;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.util.DetectorBatteryTestRunner;
import com.synopsys.integration.detect.configuration.DetectProperties;

@Tag("battery")
public class PearBattery {
    @Test
    void lock() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("pear-cli");
        test.sourceDirectoryNamed("linux-pear");
        test.sourceFileFromResource("package.xml");
        test.executableFromResourceFiles(DetectProperties.DETECT_PEAR_PATH, "pear-list.xout", "pear-package.xout");
        test.expectBdioResources();
        test.run();
    }
}

