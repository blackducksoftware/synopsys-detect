package com.synopsys.integration.detect.battery.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.BatteryTest;

@Tag("battery")
public class RubygemsBattery {
    @Test
    void lock() {
        final BatteryTest test = new BatteryTest("rubygems-lock");
        test.sourceDirectoryNamed("linux-rubygems");
        test.sourceFileFromResource("Gemfile.lock");
        test.git("https://github.com/BlackDuckCoPilot/example-rubygems-travis", "HEAD");
        test.expectBdioResources();
        test.run();
    }
}

