package com.blackduck.integration.detect.tool.detector.executable;

import com.blackduck.integration.executable.Executable;
import com.blackduck.integration.executable.ExecutableOutput;

public class ExecutedExecutable {
    private final ExecutableOutput output;
    private final Executable executable;

    public ExecutedExecutable(ExecutableOutput output, Executable executable) {
        this.output = output;
        this.executable = executable;
    }

    public ExecutableOutput getOutput() {
        return output;
    }

    public Executable getExecutable() {
        return executable;
    }
}
