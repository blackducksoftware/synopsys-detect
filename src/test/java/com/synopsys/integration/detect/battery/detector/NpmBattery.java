package com.synopsys.integration.detect.battery.detector;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.util.DetectorBatteryTestRunner;

@Tag("battery")
public class NpmBattery {
    @Test
    void packagelock() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("npm-packagelock");
        test.sourceDirectoryNamed("linux-npm");
        test.sourceFileFromResource("package-lock.json");
        test.sourceFileFromResource("package.json");
        test.git("https://github.com/BlackDuckCoPilot/example-npm-travis.git", "master");
        test.expectBdioResources();
        test.run();
    }
}

