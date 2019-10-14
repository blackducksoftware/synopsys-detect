package com.synopsys.integration.detect.battery.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.BatteryTest;

@Tag("battery")
public class GoBattery {
    @Test
    void lock() {
        final BatteryTest test = new BatteryTest("dep-lock");
        test.sourceDirectoryNamed("rooms");
        test.sourceFileFromResource("Gopkg.lock");
        test.git("https://github.com/thenrich/rooms", "master");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void conf() {
        final BatteryTest test = new BatteryTest("go_vndr-lock");
        test.sourceDirectoryNamed("linux-vndr");
        test.sourceFileFromResource("vendor.conf");
        test.git("https://github.com/moby/moby.git", "HEAD");
        test.expectBdioResources();
        test.run();
    }
}

