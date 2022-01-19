package com.synopsys.integration.detectable.detectable.executable;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.executable.Executable;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class ExecutableFailedException extends Exception {
    private static final long serialVersionUID = -4117278710469900787L;
    private final int returnCode;
    private final String executableDescription;
    private final ExecutableRunnerException executableException;

    public ExecutableFailedException(Executable executable, ExecutableRunnerException executableException) {
        this(executable, executableException, null);
    }

    public ExecutableFailedException(Executable executable, ExecutableRunnerException executableException, @Nullable String additionalFailureMessage) {
        super(createPrettyMessage("An exception occurred running an executable.", additionalFailureMessage), executableException);
        this.executableException = executableException;
        this.returnCode = 0;
        this.executableDescription = executable.getExecutableDescription();
    }

    public ExecutableFailedException(Executable executable, ExecutableOutput executableOutput) {
        this(executable, executableOutput, null);
    }

    public ExecutableFailedException(Executable executable, ExecutableOutput executableOutput, @Nullable String additionalFailureMessage) {
        super(createPrettyMessage("An executable returned a non-zero exit code: " + executableOutput.getReturnCode(), additionalFailureMessage));
        this.returnCode = executableOutput.getReturnCode();
        this.executableDescription = executable.getExecutableDescription();
        executableException = null;
    }

    private static String createPrettyMessage(String message, @Nullable String additionalFailureMessage) {
        String prettyMessage = String.format("%s %s", message, StringUtils.trimToEmpty(additionalFailureMessage));
        return StringUtils.trim(prettyMessage);
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
