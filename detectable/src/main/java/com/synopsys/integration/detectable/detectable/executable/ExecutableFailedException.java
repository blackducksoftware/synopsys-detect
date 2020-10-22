package com.synopsys.integration.detectable.detectable.executable;

import com.synopsys.integration.executable.Executable;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class ExecutableFailedException extends Exception {
    private static final long serialVersionUID = -4117278710469900787L;
    private final ExecutableOutput executableOutput;
    private final Executable executable;
    private final ExecutableRunnerException executableException;

    public ExecutableFailedException(final Executable executable, final ExecutableRunnerException executableException) {
        super(executableException);
        this.executableException = executableException;
        this.executable = executable;
        executableOutput = null;
    }

    public ExecutableFailedException(final Executable executable, ExecutableOutput executableOutput) {
        this.executableOutput = executableOutput;
        this.executable = executable;
        executableException = null;
    }

    public ExecutableOutput getExecutableOutput() {
        return executableOutput;
    }

    public Executable getExecutable() {
        return executable;
    }

    public ExecutableRunnerException getExecutableException() {
        return executableException;
    }
}