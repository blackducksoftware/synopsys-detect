package com.synopsys.integration.detect.battery.property;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.util.DetectOutput;
import com.synopsys.integration.detect.battery.util.DetectorBatteryTestRunner;
import com.synopsys.integration.detect.configuration.DetectProperties;

public class ToolsNoneBatteryTest {
    @Test
    void verifyNoneIsDeprecated() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("tools-none");
        test.sourceDirectoryNamed("tools-none");
        test.property(DetectProperties.DETECT_TOOLS, "NONE");
        DetectOutput detectOutput = test.run();

        detectOutput.assertContains("The value NONE is deprecated.");
    }
}

