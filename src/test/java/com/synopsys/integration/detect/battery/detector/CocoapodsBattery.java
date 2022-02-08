package com.synopsys.integration.detect.battery.detector;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.util.DetectorBatteryTestRunner;

@Tag("battery")
public class CocoapodsBattery {
    @Test
    void podlock() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("cocoapods-podlock");
        test.sourceDirectoryNamed("linux-cocoapods");
        test.sourceFileFromResource("Podfile.lock");
        test.git("https://github.com/BlackDuckCoPilot/example-cocoapods-travis.git", "master");
        test.expectBdioResources();
        test.run();
    }
}
