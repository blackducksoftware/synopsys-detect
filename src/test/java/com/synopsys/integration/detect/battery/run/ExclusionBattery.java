package com.synopsys.integration.detect.battery.run;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.util.BatteryTest;
import com.synopsys.integration.detect.battery.util.DetectOutput;

@Tag("battery")
class ExclusionBattery {
    @Test
    void excludesTwo() { /* Requires Signature Scanner */
        BatteryTest test = new BatteryTest("exclusions");
        test.withToolsValue("SIGNATURE_SCAN");
        test.property("detect.excluded.directories", "example");
        test.sourceDirectoryNamed("exclusions");
        test.sourceFileNamed("example1");
        test.sourceFileNamed("example2");
        DetectOutput detectOutput = test.run();

        detectOutput.assertContains("--exclude example1", "--exclude example2");
    }
}
