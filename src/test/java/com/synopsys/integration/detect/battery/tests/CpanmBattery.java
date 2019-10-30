package com.synopsys.integration.detect.battery.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.BatteryTest;

@Tag("battery")
public class CpanmBattery {
    @Test
    void lock() {
        final BatteryTest test = new BatteryTest("cpanm-lock");
        test.sourceDirectoryNamed("windows-cpanm");
        test.sourceFileFromResource("composer.json");
        test.sourceFileFromResource("composer.lock");
        test.git("https://github.com/petrkle/zonglovani.info.git", "HEAD");
        test.expectBdioResources();
        test.run();
    }
}

