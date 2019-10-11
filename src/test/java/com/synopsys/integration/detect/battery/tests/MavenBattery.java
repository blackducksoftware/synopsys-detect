package com.synopsys.integration.detect.battery.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.BatteryTest;
import com.synopsys.integration.detect.configuration.DetectProperty;

@Tag("battery")
public class MavenBattery {
    @Test
    void inspector() {
        final BatteryTest test = new BatteryTest("maven-cli");
        test.sourceDirectoryNamed("linux-maven");
        test.sourceFileNamed("pom.xml");
        test.executableFromResourceFiles(DetectProperty.DETECT_MAVEN_PATH, "maven-dependencytree.xout");
        test.git("https://github.com/BlackDuckCoPilot/example-maven-travis", "HEAD");
        test.expectBdioResources();
        test.run();
    }
}

