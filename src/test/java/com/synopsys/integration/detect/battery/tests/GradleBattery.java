package com.synopsys.integration.detect.battery.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.BatteryTest;

@Tag("battery")
public class GradleBattery {
    @Test
    void lock() {
        final BatteryTest test = new BatteryTest("gradle-inspector");
        test.sourceDirectoryNamed("linux-gradle");
        test.sourceFileNamed("build.gradle");
        test.git("https://github.com/BlackDuckCoPilot/example-gradle-travis", "HEAD");
        test.expectBdioResources();
        test.run();
    }
}

