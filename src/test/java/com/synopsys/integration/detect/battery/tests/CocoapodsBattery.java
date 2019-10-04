package com.synopsys.integration.detect.battery.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.BatteryTest;
import com.synopsys.integration.detect.configuration.DetectProperty;

@Tag("battery")
public class CocoapodsBattery {
    @Test
    void podlock() {
        final BatteryTest test = new BatteryTest("cocoapods-podlock");
        test.sourceDirectoryNamed("linux-cocoapods");
        test.sourceFileFromResource("Podfile.lock");
        test.sourceFileNamed(".git");
        test.executable(DetectProperty.DETECT_GIT_PATH, "https://github.com/BlackDuckCoPilot/example-cocoapods-travis.git", "HEAD");
        test.expectBdioResources();
        test.run();
    }
}
