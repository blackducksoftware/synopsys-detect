package com.synopsys.integration.detect.battery.run;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;

import com.synopsys.integration.detect.battery.util.DetectOutput;
import com.synopsys.integration.detect.battery.util.DetectorBattery;

@Tag("battery")
class ExclusionBattery {
    @Disabled
    void excludesTwo() { /* Requires Signature Scanner */
        DetectorBattery test = new DetectorBattery("exclusions");
        test.withToolsValue("SIGNATURE_SCAN");
        test.property("detect.excluded.directories", "example");
        test.sourceDirectoryNamed("exclusions");
        test.sourceFileNamed("example1");
        test.sourceFileNamed("example2");
        DetectOutput detectOutput = test.run();

        detectOutput.assertContains("--exclude example1", "--exclude example2");
    }
}
