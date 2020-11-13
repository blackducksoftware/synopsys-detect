package com.synopsys.integration.detect.battery.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.BatteryTest;
import com.synopsys.integration.detect.configuration.DetectProperties;

@Tag("battery")
public class ConanCliBattery {
    private static final String CONAN_MINIMAL_OUTPUT_RESOURCE = "conan-info-minimal.xout";

    @Test
    void conanMinimal() {
        BatteryTest test = new BatteryTest("conan-minimal", "conan-cli");
        test.executableFromResourceFiles(DetectProperties.DETECT_CONAN_PATH.getProperty(), CONAN_MINIMAL_OUTPUT_RESOURCE);
        test.sourceDirectoryNamed("conan-minimal");
        test.sourceFileNamed("conanfile.txt");
        test.expectBdioResources();
        test.run();
    }
}
