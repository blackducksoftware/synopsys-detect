package com.synopsys.integration.detect.battery.detector;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.util.DetectorBatteryTestRunner;

@Tag("battery")
public class CpanmBattery {
    @Test
    void lock() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("cpanm-lock");
        test.sourceDirectoryNamed("windows-cpanm");
        test.sourceFileFromResource("composer.json");
        test.sourceFileFromResource("composer.lock");
        test.git("https://github.com/petrkle/zonglovani.info.git", "master");
        test.expectBdioResources();
        test.run();
    }
}

