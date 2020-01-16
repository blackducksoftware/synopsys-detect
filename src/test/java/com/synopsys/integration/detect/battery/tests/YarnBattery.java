package com.synopsys.integration.detect.battery.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.BatteryTest;

@Tag("battery")
public class YarnBattery {
    @Test
    void lock() {
        final BatteryTest test = new BatteryTest("yarn-lock");
        test.sourceDirectoryNamed("linux-yarn");
        test.sourceFileFromResource("yarn.lock");
        test.sourceFileFromResource("package.json");
        test.git("https://github.com/babel/babel", "master");
        test.expectBdioResources();
        test.run();
    }
}

