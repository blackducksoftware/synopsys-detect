package com.synopsys.integration.detect.battery.property;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.util.DetectOutput;
import com.synopsys.integration.detect.battery.util.DetectorBatteryTestRunner;
import com.synopsys.integration.detect.configuration.DetectProperties;

public class ToolsNoneBatteryTest {
    @Disabled
    @Test
    void verifyNoneIsNotAValidValue() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("tools-none");
        test.sourceDirectoryNamed("tools-none");
        test.property(DetectProperties.DETECT_TOOLS, "NONE");
        DetectOutput detectOutput = test.run();

        detectOutput.assertContains("Overall Status: FAILURE_CONFIGURATION");
    }
}

