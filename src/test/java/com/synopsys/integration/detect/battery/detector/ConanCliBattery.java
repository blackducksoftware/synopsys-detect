package com.synopsys.integration.detect.battery.detector;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.util.DetectorBatteryTestRunner;
import com.synopsys.integration.detect.configuration.DetectProperties;

@Tag("battery")
public class ConanCliBattery {

    // TODO add test(s) for test.property("detect.conan.require.prev.match", "false");

    @Test
    void conanMinimal() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("conan-minimal", "conan-cli/minimal");
        test.executableFromResourceFiles(DetectProperties.DETECT_CONAN_PATH, "conan-info-minimal.xout");
        test.sourceDirectoryNamed("conan-minimal");
        test.sourceFileNamed("conanfile.txt");
        test.property("detect.conan.attempt.package.revision.match", "true");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void conanWithProjectNameVersion() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("conan-withprojectnameversion", "conan-cli/withprojectnameversion");
        test.executableFromResourceFiles(DetectProperties.DETECT_CONAN_PATH, "conan-info-withprojectnameversion.xout");
        test.sourceDirectoryNamed("conan-withprojectnameversion");
        test.sourceFileNamed("conanfile.txt");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void conanWithUserChannel() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("conan-withuserchannel", "conan-cli/withuserchannel");
        test.executableFromResourceFiles(DetectProperties.DETECT_CONAN_PATH, "conan-info-withuserchannel.xout");
        test.sourceDirectoryNamed("conan-withuserchannel");
        test.sourceFileNamed("conanfile.txt");
        test.property("detect.conan.attempt.package.revision.match", "true");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void conanWithRevisions() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("conan-withrevisions", "conan-cli/withrevisions");
        test.executableFromResourceFiles(DetectProperties.DETECT_CONAN_PATH, "conan-info-withrevisions.xout");
        test.sourceDirectoryNamed("conan-withrevisions");
        test.sourceFileNamed("conanfile.py");
        test.property("detect.conan.attempt.package.revision.match", "true");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void conanPkgRevOnly() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("conan-pkgrevonly", "conan-cli/pkgrevonly");
        test.executableFromResourceFiles(DetectProperties.DETECT_CONAN_PATH, "conan-info-pkgrevonly.xout");
        test.sourceDirectoryNamed("conan-pkgrevonly");
        test.sourceFileNamed("conanfile.txt");
        test.property("detect.conan.attempt.package.revision.match", "true");
        test.expectBdioResources();
        test.run();
    }
}
