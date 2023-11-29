package com.synopsys.integration.detect.battery.detector;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.util.DetectorBatteryTestRunner;
import com.synopsys.integration.detect.configuration.DetectProperties;

@Tag("battery")
public class ConanCliBattery {
    private static final String CONAN1_VERSION_OUTPUT_RESOURCE = "../conan1-version.xout";

    @Test
    void conanMinimal() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("conan-minimal", "conan-cli/minimal");
        setTestExecutableOutputForConan1(test, "conan-info-minimal.xout");
        test.sourceDirectoryNamed("conan-minimal");
        test.sourceFileNamed("conanfile.txt");
        test.property("detect.conan.attempt.package.revision.match", "true");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void conanWithProjectNameVersion() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("conan-withprojectnameversion", "conan-cli/withprojectnameversion");
        setTestExecutableOutputForConan1(test, "conan-info-withprojectnameversion.xout");
        test.sourceDirectoryNamed("conan-withprojectnameversion");
        test.sourceFileNamed("conanfile.txt");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void conanWithUserChannel() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("conan-withuserchannel", "conan-cli/withuserchannel");
        setTestExecutableOutputForConan1(test, "conan-info-withuserchannel.xout");
        test.sourceDirectoryNamed("conan-withuserchannel");
        test.sourceFileNamed("conanfile.txt");
        test.property("detect.conan.attempt.package.revision.match", "true");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void conanWithRevisions() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("conan-withrevisions", "conan-cli/withrevisions");
        setTestExecutableOutputForConan1(test, "conan-info-withrevisions.xout");
        test.sourceDirectoryNamed("conan-withrevisions");
        test.sourceFileNamed("conanfile.py");
        test.property("detect.conan.attempt.package.revision.match", "true");
        test.expectBdioResources();
        test.run();
    }

    @Test
    void conanPkgRevOnly() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("conan-pkgrevonly", "conan-cli/pkgrevonly");
        setTestExecutableOutputForConan1(test, "conan-info-pkgrevonly.xout");
        test.sourceDirectoryNamed("conan-pkgrevonly");
        test.sourceFileNamed("conanfile.txt");
        test.property("detect.conan.attempt.package.revision.match", "true");
        test.expectBdioResources();
        test.run();
    }

    private void setTestExecutableOutputForConan1(DetectorBatteryTestRunner test, String resource) {
        test.executableFromResourceFiles(
            DetectProperties.DETECT_CONAN_PATH,
            CONAN1_VERSION_OUTPUT_RESOURCE,
            CONAN1_VERSION_OUTPUT_RESOURCE, // version output is checked twice: once by Conan 2 detectable and once by Conan 1
            resource
        );
    }
}
