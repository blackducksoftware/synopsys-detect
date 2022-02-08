package com.synopsys.integration.detectable.util;

import com.synopsys.integration.executable.ExecutableOutput;

public class ExecutableOutputUtil {
    public static ExecutableOutput success(String line) {
        return new ExecutableOutput(0, line, "");
    }
}
