package com.synopsys.integration.detect.battery.detector;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.util.DetectorBattery;
import com.synopsys.integration.detect.configuration.DetectProperties;

@Tag("battery")
public class ConanCliBattery {

    // TODO add test(s) for test.property("detect.conan.require.prev.match", "false");

    @Test
    void conanMinimal() {
        DetectorBattery test = new DetectorBattery("conan-minimal", "conan-cli/minimal");
        test.executableFromResourceFiles(DetectProperties.DETECT_CONAN_PATH.getProperty(), "conan-info-minimal.xout");
        test.sourceDirectoryNamed("conan-minimal");
        test.sourceFileNamed("conanfile.txt");
        test.property("detect.conan.attempt.package.revision.match", "true");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void conanWithProjectNameVersion() {
        DetectorBattery test = new DetectorBattery("conan-withprojectnameversion", "conan-cli/withprojectnameversion");
        test.executableFromResourceFiles(DetectProperties.DETECT_CONAN_PATH.getProperty(), "conan-info-withprojectnameversion.xout");
        test.sourceDirectoryNamed("conan-withprojectnameversion");
        test.sourceFileNamed("conanfile.txt");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void conanWithUserChannel() {
        DetectorBattery test = new DetectorBattery("conan-withuserchannel", "conan-cli/withuserchannel");
        test.executableFromResourceFiles(DetectProperties.DETECT_CONAN_PATH.getProperty(), "conan-info-withuserchannel.xout");
        test.sourceDirectoryNamed("conan-withuserchannel");
        test.sourceFileNamed("conanfile.txt");
        test.property("detect.conan.attempt.package.revision.match", "true");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void conanWithRevisions() {
        DetectorBattery test = new DetectorBattery("conan-withrevisions", "conan-cli/withrevisions");
        test.executableFromResourceFiles(DetectProperties.DETECT_CONAN_PATH.getProperty(), "conan-info-withrevisions.xout");
        test.sourceDirectoryNamed("conan-withrevisions");
        test.sourceFileNamed("conanfile.py");
        test.property("detect.conan.attempt.package.revision.match", "true");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void conanPkgRevOnly() {
        DetectorBattery test = new DetectorBattery("conan-pkgrevonly", "conan-cli/pkgrevonly");
        test.executableFromResourceFiles(DetectProperties.DETECT_CONAN_PATH.getProperty(), "conan-info-pkgrevonly.xout");
        test.sourceDirectoryNamed("conan-pkgrevonly");
        test.sourceFileNamed("conanfile.txt");
        test.property("detect.conan.attempt.package.revision.match", "true");
        test.expectBdioResources();
        test.run();
    }
}
