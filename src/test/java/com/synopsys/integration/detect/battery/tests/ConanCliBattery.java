package com.synopsys.integration.detect.battery.tests;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.BatteryTest;
import com.synopsys.integration.detect.configuration.DetectProperties;

@Tag("battery")
public class ConanCliBattery {

    // TODO add test(s) for test.property("detect.conan.require.prev.match", "false");

    @Test
    void conanMinimal() {
        BatteryTest test = new BatteryTest("conan-minimal", "conan-cli/minimal");
        test.executableFromResourceFiles(DetectProperties.DETECT_CONAN_PATH.getProperty(), "conan-info-minimal.xout");
        test.sourceDirectoryNamed("conan-minimal");
        test.sourceFileNamed("conanfile.txt");
        test.property("detect.conan.require.package.revision.match", "true");
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
        test.property("detect.conan.require.package.revision.match", "true");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void conanWithRevisions() {
        BatteryTest test = new BatteryTest("conan-withrevisions", "conan-cli/withrevisions");
        test.executableFromResourceFiles(DetectProperties.DETECT_CONAN_PATH.getProperty(), "conan-info-withrevisions.xout");
        test.sourceDirectoryNamed("conan-withrevisions");
        test.sourceFileNamed("conanfile.py");
        test.property("detect.conan.require.package.revision.match", "true");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void conanPkgRevOnly() {
        BatteryTest test = new BatteryTest("conan-pkgrevonly", "conan-cli/pkgrevonly");
        test.executableFromResourceFiles(DetectProperties.DETECT_CONAN_PATH.getProperty(), "conan-info-pkgrevonly.xout");
        test.sourceDirectoryNamed("conan-pkgrevonly");
        test.sourceFileNamed("conanfile.txt");
        test.property("detect.conan.require.package.revision.match", "true");
        test.expectBdioResources();
        test.run();
    }
}
