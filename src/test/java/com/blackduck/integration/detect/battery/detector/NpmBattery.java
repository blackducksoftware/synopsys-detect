package com.blackduck.integration.detect.battery.detector;

import com.blackduck.integration.detect.battery.util.DetectorBatteryTestRunner;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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

