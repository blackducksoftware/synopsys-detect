package com.synopsys.integration.detect.battery.util;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.detect.workflow.report.output.FormattedOutput;

public class DetectOutput {
    private final List<String> standardOutput;
    private final File sourceDirectory;
    private final FormattedOutput statusJson;

    public DetectOutput(List<String> standardOutput, File sourceDirectory, FormattedOutput statusJson) {
        this.standardOutput = standardOutput;
        this.sourceDirectory = sourceDirectory;
        this.statusJson = statusJson;
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

    public void assertContainsBlock(String... data) {
        boolean fnd = false;
        for (int i = 0; i < standardOutput.size(); i++) {
            boolean blockFnd = false;
            for (int d = 0; d < data.length; d++) {
                if (i + d > standardOutput.size())
                    break;
                if (!standardOutput.get(i + d).contains(data[d])) {
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

    public File getSourceDirectory() {
        return sourceDirectory;
    }

    public FormattedOutput getStatusJson() {
        return statusJson;
    }
}
