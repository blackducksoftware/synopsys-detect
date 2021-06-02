package com.synopsys.integration.detect.battery;

import java.util.List;

import org.junit.jupiter.api.Assertions;

public class DetectOutput {
    private final List<String> standardOutput;

    public DetectOutput(final List<String> standardOutput) {
        this.standardOutput = standardOutput;
    }

    public void assertContains(String arg) {
        boolean fnd = false;
        for (String line : standardOutput) {
            fnd = fnd || line.contains(arg);
        }
        Assertions.assertTrue(fnd, "String '" + arg + "' must be found in the logs.");
    }

    public void assertContains(String... args) {
        for (String arg : args) {
            assertContains(arg);
        }
    }
}
