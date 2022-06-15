package com.synopsys.integration.detect.battery.accuracy;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.configuration.property.types.enumallnone.enumeration.AllNoneEnum;
import com.synopsys.integration.detect.battery.util.DetectOutput;
import com.synopsys.integration.detect.battery.util.DetectorBatteryTestRunner;
import com.synopsys.integration.detect.battery.util.assertions.FormattedDetectorAssert;
import com.synopsys.integration.detect.battery.util.assertions.FormattedOutputAssert;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detector.base.DetectorStatusCode;

@Tag("battery")
class CascadeBatteryTests {
    @Test
    void ivyExtractionFailure() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("ivy-failure", "none");
        test.withToolsValue("DETECTOR");
        test.sourceDirectoryNamed("ivy");
        test.sourceFileNamed("ivy.xml");
        DetectOutput output = test.run();

        output.assertContains("IVY: FAILURE", "Overall Status: FAILURE_DETECTOR");
        output.assertContainsBlock(
            "DETECTORS:",
            "Detector Issue",
            "EXTRACTION_FAILED: Ivy Build Parse"
        );

        FormattedOutputAssert statusAssert = new FormattedOutputAssert(output.getStatusJson());
        statusAssert.assertDetectorCount(1, "Expected a single IVY detector, this may mean you just need to add new ivy detectors to the assert.");

        FormattedDetectorAssert ivy = statusAssert.detectorAssertAtIndex(0);
        ivy.assertStatus("FAILURE", "Ivy should not be extracted, it was an empty file.");
        ivy.assertStatusCode(DetectorStatusCode.EXTRACTION_FAILED, "Ivy should have thrown an exception parsing an empty file.");
    }

    @Test
    void npmLockCascadesToParseButDoesNotMeetAccuracy() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("npm-lock-failure-cascade-not-met", "none");
        test.withToolsValue("DETECTOR");
        test.sourceDirectoryNamed("npm");
        test.sourceFileNamed("package.json", "{}");
        DetectOutput output = test.run();

        output.assertContains("NPM Package Json Parse: SUCCESS");

        output.assertContains("NPM CLI: ATTEMPTED");
        output.assertContains("but the node_modules folder was NOT located");

        output.assertContains("NPM: SUCCESS");
        output.assertContains("Overall Status: FAILURE_ACCURACY_NOT_MET - Detect was unable to meet the required accuracy.");
    }

    @Test
    void npmCascadesToParseWithNoneAccuracy() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("npm-lock-failure-cascade-met", "none");
        test.withToolsValue("DETECTOR");
        test.property(DetectProperties.DETECT_ACCURACY_REQUIRED, AllNoneEnum.NONE.toString());
        test.sourceDirectoryNamed("npm");
        test.sourceFileNamed("package.json", "{}");
        DetectOutput output = test.run();

        output.assertContains("NPM Package Json Parse: SUCCESS");
        output.assertContains("NPM CLI: ATTEMPTED");

        output.assertContains("NPM: SUCCESS");
        output.assertContains("Overall Status: SUCCESS");
    }

    @Test
    void npmShrinkwrapCascadesToParseDoesNotMeetAccuracy() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("npm-lock-failure-cascade-shrink-notmet", "none");
        test.withToolsValue("DETECTOR");
        test.sourceDirectoryNamed("npm");
        test.sourceFileNamed("package.json", "{}");
        test.sourceFileNamed("npm-shrinkwrap.json", "bad{json}");
        DetectOutput output = test.run();

        output.assertContains("NPM Package Json Parse: SUCCESS");
        output.assertContains("NPM Shrinkwrap: ATTEMPTED");
        output.assertContains("NPM CLI: ATTEMPTED");

        output.assertContains("NPM: SUCCESS");
        output.assertContains("Overall Status: FAILURE_ACCURACY_NOT_MET - Detect was unable to meet the required accuracy.");
    }

    @Test
    void npmShrinkwrapCascadesToParseWithNoneAccuracy() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("npm-lock-failure-cascade-shrink", "none");
        test.withToolsValue("DETECTOR");
        test.property(DetectProperties.DETECT_ACCURACY_REQUIRED, AllNoneEnum.NONE.toString());
        test.sourceDirectoryNamed("npm");
        test.sourceFileNamed("package.json", "{}");
        test.sourceFileNamed("npm-shrinkwrap.json", "bad{json}");
        DetectOutput output = test.run();

        output.assertContains("NPM Package Json Parse: SUCCESS");
        output.assertContains("NPM Shrinkwrap: ATTEMPTED");
        output.assertContains("NPM CLI: ATTEMPTED");

        output.assertContains("NPM: SUCCESS");
        output.assertContains("Overall Status: SUCCESS");
    }

    @Test
    void npmEverythingFails() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("npm-all-failure", "none");
        test.withToolsValue("DETECTOR");
        test.sourceDirectoryNamed("npm");
        test.sourceFileNamed("package.json", "bad{json}");
        test.sourceFileNamed("npm-shrinkwrap.json", "bad{json}");
        DetectOutput output = test.run();

        output.assertContains("NPM Package Json Parse: FAILED");
        output.assertContainsBlock(
            "Detector Issue",
            "EXTRACTION_FAILED: NPM Package Json Parse"
        );

        output.assertContains("NPM Shrinkwrap: FAILED");
        output.assertContainsBlock(
            "Detector Issue",
            "EXTRACTION_FAILED: NPM Shrinkwrap"
        );

        output.assertContains("NPM CLI: FAILED");
        output.assertContainsBlock(
            "Detector Issue",
            "EXTRACTION_FAILED: NPM Package Json Parse"
        );

        output.assertContains("NPM: FAILURE");
        output.assertContains("Overall Status: FAILURE_DETECTOR");
    }
}
