package com.synopsys.integration.detect.battery.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.BatteryTest;
import com.synopsys.integration.detect.configuration.DetectProperty;

@Tag("battery")
public class MavenBattery {
    private static final String MAVEN_OUTPUT_RESOURCE = "maven-dependencytree.xout";

    @Test
    void property() {
        final BatteryTest test = sharedCliTest("maven-property");
        test.executableFromResourceFiles(DetectProperty.DETECT_MAVEN_PATH, MAVEN_OUTPUT_RESOURCE);
        test.run();
    }

    @Test
    void wrapper() {
        final BatteryTest test = sharedCliTest("maven-wrapper");
        test.executableSourceFileFromResourceFiles("mvnw.cmd", "mvnw", MAVEN_OUTPUT_RESOURCE);
        test.run();
    }

    @Test
    BatteryTest sharedCliTest(final String name) {
        final BatteryTest test = new BatteryTest(name, "maven-cli");
        test.sourceDirectoryNamed("linux-maven");
        test.sourceFileNamed("pom.xml");
        test.git("https://github.com/BlackDuckCoPilot/example-maven-travis", "HEAD");
        test.expectBdioResources();
        return test;
    }
}

