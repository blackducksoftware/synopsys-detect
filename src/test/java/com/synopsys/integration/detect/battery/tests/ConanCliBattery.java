package com.synopsys.integration.detect.battery.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.BatteryTest;
import com.synopsys.integration.detect.configuration.DetectProperties;

@Tag("battery")
public class ConanCliBattery {

    @Test
    void conanMinimal() {
        BatteryTest test = new BatteryTest("conan-minimal", "conan-cli/minimal");
        test.executableFromResourceFiles(DetectProperties.DETECT_CONAN_PATH.getProperty(), "conan-info-minimal.xout");
        test.sourceDirectoryNamed("conan-minimal");
        test.sourceFileNamed("conanfile.txt");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void conanWithProjectNameVersion() {
        BatteryTest test = new BatteryTest("conan-withprojectnameversion", "conan-cli/withprojectnameversion");
        test.executableFromResourceFiles(DetectProperties.DETECT_CONAN_PATH.getProperty(), "conan-info-withprojectnameversion.xout");
        test.sourceDirectoryNamed("conan-withprojectnameversion");
        test.sourceFileNamed("conanfile.txt");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void conanWithUserChannel() {
        BatteryTest test = new BatteryTest("conan-withuserchannel", "conan-cli/withuserchannel");
        test.executableFromResourceFiles(DetectProperties.DETECT_CONAN_PATH.getProperty(), "conan-info-withuserchannel.xout");
        test.sourceDirectoryNamed("conan-withuserchannel");
        test.sourceFileNamed("conanfile.txt");
        test.expectBdioResources();
        test.run();
    }
}
