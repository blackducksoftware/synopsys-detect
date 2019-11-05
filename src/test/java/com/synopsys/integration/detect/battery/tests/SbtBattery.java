package com.synopsys.integration.detect.battery.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.BatteryTest;

@Tag("battery")
public class SbtBattery {
    @Test
    void resolutioncache() {
        final BatteryTest test = new BatteryTest("sbt-resolutioncache");
        test.sourceDirectoryNamed("linux-sbt");
        test.sourceFileNamed("build.sbt");
        test.sourceFolderFromExpandedResource("target");
        test.git("https://github.com/sbt/sbt-bintray.git", "master");
        test.expectBdioResources();
        test.run();
    }
}

