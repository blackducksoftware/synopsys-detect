package com.synopsys.integration.detect.battery.accuracy;

import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.detect.workflow.report.output.FormattedOutput;

public class FormattedOutputAssertions {
    private final FormattedOutput statusJson;

    public FormattedOutputAssertions(FormattedOutput statusJson) {this.statusJson = statusJson;}

    public void assertDetectorCount(int count, String message) {
        Assertions.assertEquals(
            count,
            statusJson.detectors.size(),
            message
        );
    }

    public FormattedDetectorAssertions detectorAssertAtIndex(int i) {
        Assertions.assertTrue(statusJson.detectors.size() > i, "Not enough detectors to assert on!");
        return new FormattedDetectorAssertions(statusJson.detectors.get(i));
    }
}
