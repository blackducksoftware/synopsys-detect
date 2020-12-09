package com.synopsys.integration.detect.battery.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.BatteryTest;

@Tag("battery")
public class ConanLockfileBattery {

    @Test
    void conanLock() {
        BatteryTest test = new BatteryTest("conan-lock");
        test.sourceDirectoryNamed("conan-lock");
        test.sourceFileFromResource("conan.lock");
        test.expectBdioResources();
        test.run();
    }
}
