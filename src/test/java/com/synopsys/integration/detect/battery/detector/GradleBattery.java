package com.synopsys.integration.detect.battery.detector;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.util.DetectorBatteryTestRunner;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detectable.detectables.gradle.inspection.GradleConfigurationType;

@Tag("battery")
public class GradleBattery {
    private static final String RESOURCE_FOLDER = "GRADLE-0";

    @Test
    void gradleFromProperty() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("gradle-property", "gradle-inspector");
        test.executableThatCopiesFiles(DetectProperties.DETECT_GRADLE_PATH, RESOURCE_FOLDER)
            .onWindows(5, "")
            .onLinux(3, "-DGRADLEEXTRACTIONDIR=");
        test.sourceDirectoryNamed("linux-gradle");
        test.sourceFileNamed("build.gradle");
        test.git("https://github.com/BlackDuckCoPilot/example-gradle-travis", "master");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void gradleWrapperFromSourceFile() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("gradle-wrapper", "gradle-inspector");
        test.executableSourceFileThatCopiesFiles("gradlew.bat", "gradlew", RESOURCE_FOLDER)
            .onWindows(5, "")
            .onLinux(3, "-DGRADLEEXTRACTIONDIR=");
        test.sourceDirectoryNamed("linux-gradle");
        test.sourceFileNamed("build.gradle");
        test.git("https://github.com/BlackDuckCoPilot/example-gradle-travis", "master");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void gradleWrapperOnDetect() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("gradle-detect-on-detect", "gradle-detect-on-detect");
        test.executableSourceFileThatCopiesFiles("gradlew.bat", "gradlew", RESOURCE_FOLDER)
            .onWindows(5, "")
            .onLinux(3, "-DGRADLEEXTRACTIONDIR=");
        test.sourceDirectoryNamed("synopsys-detect");
        test.sourceFileNamed("build.gradle");
        test.property(DetectProperties.DETECT_GRADLE_CONFIGURATION_TYPES_EXCLUDED, GradleConfigurationType.UNRESOLVED.name());
        test.git("https://github.com/blackducksoftware/synopsys-detect", "master");
        test.expectBdioResources();
        test.run();
    }
}

