package com.synopsys.integration.detect.battery.util.assertions;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;

public class OutputAssert {
    private final List<String> output;

    public OutputAssert(List<String> output) {
        this.output = output;
    }

    public void assertContains(String arg) {
        boolean fnd = false;
        for (String line : output) {
            fnd = fnd || line.contains(arg);
        }
        Assertions.assertTrue(fnd, "String '" + arg + "' must be found in the logs.");
    }

    public void assertContains(String... args) {
        for (String arg : args) {
            assertContains(arg);
        }
    }

    public void assertContainsBlock(String... data) {
        boolean fnd = false;
        for (int i = 0; i < output.size(); i++) {
            boolean blockFnd = false;
            for (int d = 0; d < data.length; d++) {
                if (i + d > output.size())
                    break;
                if (!output.get(i + d).contains(data[d])) {
                    break;
                }
                if (d == data.length - 1) {
                    blockFnd = true;
                    break;
                }
            }
            if (blockFnd) {
                fnd = true;
                break;
            }
        }
        Assertions.assertTrue(fnd, "Block '\n" + StringUtils.join(data, "\n") + "' must be found in the logs.");
    }
}
