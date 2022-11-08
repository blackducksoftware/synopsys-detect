package com.synopsys.integration.detect.battery.util.assertions;

import java.util.function.Predicate;

import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.detect.workflow.report.output.FormattedDetectorOutput;
import com.synopsys.integration.detect.workflow.report.output.FormattedOutput;
import com.synopsys.integration.detector.base.DetectorStatusCode;

public class FormattedOutputAssert {
    private final FormattedOutput statusJson;

    public FormattedOutputAssert(FormattedOutput statusJson) {this.statusJson = statusJson;}

    public void assertDetectorCount(int count, String message) {
        Assertions.assertEquals(
            count,
            statusJson.detectors.size(),
            message
        );
    }

    public FormattedDetectorAssert detectorAssertAtIndex(int i) {
        Assertions.assertTrue(statusJson.detectors.size() > i, "Not enough detectors to assert on!");
        return new FormattedDetectorAssert(statusJson.detectors.get(i));
    }

    public void assertDetectableStatusNamed(String name, DetectorStatusCode statusCode) {
        assertDetectableStatus(detectable -> detectable.detectorName.equals(name), statusCode);
    }

    public void assertDetectableStatus(Predicate<FormattedDetectorOutput> detectableCondition, DetectorStatusCode statusCode) {
        boolean fnd = false;
        for (FormattedDetectorOutput detector : statusJson.detectors) {
            if (detectableCondition.test(detector)) {
                fnd = true;
                Assertions.assertEquals(detector.statusCode, statusCode);
            }
        }
        Assertions.assertTrue(fnd, "Could not find detector in the status json.");
    }
}
