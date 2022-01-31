package com.synopsys.integration.detect.battery.detector;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.util.DetectorBatteryTestRunner;

@Tag("battery")
public class RubygemsBattery {
    @Test
    void lock() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("rubygems-lock");
        test.sourceDirectoryNamed("linux-rubygems");
        test.sourceFileFromResource("Gemfile.lock");
        test.git("https://github.com/BlackDuckCoPilot/example-rubygems-travis", "master");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void gemfileGeneratingCircularDependencies() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("rubygems-circular-lock");
        test.sourceDirectoryNamed("jquery-multiselect-rails");
        test.sourceFileFromResource("Gemfile.lock");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void gemfileVersionLessDependencies() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("rubygems-versionless-lock");
        test.sourceDirectoryNamed("rails");
        test.sourceFileFromResource("Gemfile.lock");
        test.expectBdioResources();
        test.run();
    }

}

