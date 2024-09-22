package com.blackduck.integration.detectable.util;

import com.blackduck.integration.executable.ExecutableOutput;

public class ExecutableOutputUtil {
    public static ExecutableOutput success(String line) {
        return new ExecutableOutput(0, line, "");
    }
}
