package com.synopsys.integration.detect.battery.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.BatteryTest;
import com.synopsys.integration.detect.configuration.DetectProperty;

@Tag("battery")
public class GradleBattery {
    @Test
    void inspector() {
        //Note about this test: The paths have been removed from the inspector meta data.
        final BatteryTest test = new BatteryTest("gradle-inspector");
        test.sourceDirectoryNamed("linux-gradle");
        test.sourceFileNamed("build.gradle");
        test.executableThatCopiesFiles(DetectProperty.DETECT_GRADLE_PATH, 3, "-DGRADLEEXTRACTIONDIR=", "GRADLE-0");
        test.git("https://github.com/BlackDuckCoPilot/example-gradle-travis", "HEAD");
        test.expectBdioResources();
        test.run();
    }
}

