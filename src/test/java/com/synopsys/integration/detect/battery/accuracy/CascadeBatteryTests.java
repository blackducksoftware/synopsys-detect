package com.synopsys.integration.detect.battery.accuracy;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.util.DetectOutput;
import com.synopsys.integration.detect.battery.util.DetectorBatteryTestRunner;
import com.synopsys.integration.detector.base.DetectorStatusCode;

@Tag("battery")
class CascadeBatteryTests {
    @Test
    void ivyExtractionFailure() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("bazel-maven-install", "bazel/maven-install");
        test.withToolsValue("DETECTOR");
        test.sourceDirectoryNamed("single-failure");
        test.sourceFileNamed("ivy.xml");
        DetectOutput output = test.run();

        output.assertContains("IVY: FAILURE", "Overall Status: FAILURE_DETECTOR");
        output.assertContainsBlock(
            "DETECTORS:",
            "Detector Issue",
            "EXTRACTION_FAILED: Ivy Build Parse"
        );

        FormattedOutputAssertions statusAssert = new FormattedOutputAssertions(output.getStatusJson());
        statusAssert.assertDetectorCount(1, "Expected a single IVY detector, this may mean you just need to add new ivy detectors to the assert.");

        FormattedDetectorAssertions ivy = statusAssert.detectorAssertAtIndex(0);
        ivy.assertStatus("FAILURE", "Ivy should not be extracted, it was an empty file.");
        ivy.assertStatusCode(DetectorStatusCode.EXTRACTION_FAILED, "Ivy should have thrown an exception parsing an empty file.");
    }
}
