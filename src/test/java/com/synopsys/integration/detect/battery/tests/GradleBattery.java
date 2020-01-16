package com.synopsys.integration.detect.battery.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.BatteryTest;
import com.synopsys.integration.detect.battery.ResourceCopyingExecutableCreator;
import com.synopsys.integration.detect.configuration.DetectProperties;

@Tag("battery")
public class GradleBattery {
    private static final String RESOURCE_FOLDER = "GRADLE-0";

    @Test
    void property() {
        final BatteryTest test = sharedInspectorTest("gradle-property");
        addGradleArguments(test.executableThatCopiesFiles(DetectProperties.Companion.getDETECT_GRADLE_PATH(), RESOURCE_FOLDER));
        test.run();
    }

    @Test
    void wrapper() {
        final BatteryTest test = sharedInspectorTest("gradle-wrapper");
        addGradleArguments(test.executableSourceFileThatCopiesFiles("gradlew.bat", "gradlew", RESOURCE_FOLDER));
        test.run();
    }

    //Note about this test: The paths have been removed from the inspector meta data.
    BatteryTest sharedInspectorTest(final String name) {
        final BatteryTest test = new BatteryTest(name, "gradle-inspector");
        test.sourceDirectoryNamed("linux-gradle");
        test.sourceFileNamed("build.gradle");
        test.git("https://github.com/BlackDuckCoPilot/example-gradle-travis", "master");
        test.expectBdioResources();
        return test;
    }

    void addGradleArguments(final ResourceCopyingExecutableCreator resourceCopyingExecutableCreator) {
        resourceCopyingExecutableCreator.onWindows(5, "").onLinux(3, "-DGRADLEEXTRACTIONDIR=");
    }
}

