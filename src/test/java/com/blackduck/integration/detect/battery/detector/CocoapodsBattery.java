package com.blackduck.integration.detect.battery.detector;

import com.blackduck.integration.detect.battery.util.DetectorBatteryTestRunner;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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
