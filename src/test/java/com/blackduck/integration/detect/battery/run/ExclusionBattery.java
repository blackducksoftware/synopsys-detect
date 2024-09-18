package com.blackduck.integration.detect.battery.run;

import com.blackduck.integration.detect.battery.util.DetectorBatteryTestRunner;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;

import com.blackduck.integration.detect.battery.util.DetectOutput;

@Tag("battery")
class ExclusionBattery {
    @Disabled
    void excludesTwo() { /* Requires Signature Scanner */
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("exclusions");
        test.withToolsValue("SIGNATURE_SCAN");
        test.property("detect.excluded.directories", "example");
        test.sourceDirectoryNamed("exclusions");
        test.sourceFileNamed("example1");
        test.sourceFileNamed("example2");
        DetectOutput detectOutput = test.run();

        detectOutput.assertContains("--exclude example1", "--exclude example2");
    }
}
