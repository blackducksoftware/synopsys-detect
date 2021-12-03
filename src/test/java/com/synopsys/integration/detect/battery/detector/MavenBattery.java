package com.synopsys.integration.detect.battery.detector;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.util.DetectorBatteryTestRunner;
import com.synopsys.integration.detect.configuration.DetectProperties;

@Tag("battery")
public class MavenBattery {
    private static final String MAVEN_OUTPUT_RESOURCE = "maven-dependencytree.xout";

    @Test
    void mavenFromProperty() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("maven-property", "maven-cli");
        test.executableFromResourceFiles(DetectProperties.DETECT_MAVEN_PATH.getProperty(), MAVEN_OUTPUT_RESOURCE);
        test.sourceDirectoryNamed("linux-maven");
        test.sourceFileNamed("pom.xml");
        test.git("https://github.com/BlackDuckCoPilot/example-maven-travis", "master");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void mavenFromSourceFile() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("maven-wrapper", "maven-cli");
        test.executableSourceFileFromResourceFiles("mvnw.cmd", "mvnw", MAVEN_OUTPUT_RESOURCE);
        test.sourceDirectoryNamed("linux-maven");
        test.sourceFileNamed("pom.xml");
        test.git("https://github.com/BlackDuckCoPilot/example-maven-travis", "master");
        test.expectBdioResources();
        test.run();
    }
}

