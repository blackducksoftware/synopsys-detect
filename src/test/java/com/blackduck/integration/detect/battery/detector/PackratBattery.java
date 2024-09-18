package com.blackduck.integration.detect.battery.detector;

import com.blackduck.integration.detect.battery.util.DetectorBatteryTestRunner;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("battery")
public class PackratBattery {
    @Test
    void lock() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("packrat-lock");
        test.sourceDirectoryNamed("packrat-lock");
        test.sourceFileFromResource("packrat.lock");
        test.git("https://github.com/pingles/redshift-r.git", "master");
        test.expectBdioResources();
        test.run();
    }
}

