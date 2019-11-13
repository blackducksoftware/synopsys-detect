package com.synopsys.integration.detect.battery.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.BatteryTest;

@Tag("battery")
public class PackratBattery {
    @Test
    void lock() {
        final BatteryTest test = new BatteryTest("packrat-lock");
        test.sourceDirectoryNamed("packrat-lock");
        test.sourceFileFromResource("packrat.lock");
        test.git("https://github.com/pingles/redshift-r.git", "master");
        test.expectBdioResources();
        test.run();
    }
}

