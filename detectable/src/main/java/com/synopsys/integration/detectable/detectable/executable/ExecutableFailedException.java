package com.synopsys.integration.detectable.detectable.executable;

import com.synopsys.integration.executable.Executable;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class ExecutableFailedException extends Exception {
    private static final long serialVersionUID = -4117278710469900787L;
    private final int returnCode;
    private final String executableDescription;
    private final ExecutableRunnerException executableException;

    public ExecutableFailedException(Executable executable, ExecutableRunnerException executableException) {
        super("An exception occurred running an executable.", executableException);
        this.executableException = executableException;
        this.returnCode = 0;
        this.executableDescription = executable.getExecutableDescription();
    }

    public ExecutableFailedException(Executable executable, ExecutableOutput executableOutput) {
        super("An executable returned a non-zero exit code: " + executableOutput.getReturnCode());
        this.returnCode = executableOutput.getReturnCode();
        this.executableDescription = executable.getExecutableDescription();
        executableException = null;
    }

    public boolean hasReturnCode() {
        return returnCode != 0;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public String getExecutableDescription() {
        return executableDescription;
    }

    public ExecutableRunnerException getExecutableException() {
        return executableException;
    }
}
