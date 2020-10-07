package com.synopsys.integration.detectable.detectable.executable;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExecutableResult {
    @Nullable
    private final Executable target;
    @Nullable
    private final Exception exception;
    @Nullable
    private final ExecutableOutput executableOutput;

    private ExecutableResult(@NotNull final Executable target, final @Nullable Exception exception, final @Nullable ExecutableOutput executableOutput) {
        this.target = target;
        this.exception = exception;
        this.executableOutput = executableOutput;
    }

    public static ExecutableResult wrap(final ExecutableRunner executableRunner, final File directory, final File exe, final List<String> arguments) {
        Executable executable = executableRunner.translate(directory, exe, arguments);
        try {
            ExecutableOutput executableOutput = executableRunner.execute(executable);
            return ExecutableResult.success(executable, executableOutput);
        } catch (ExecutableRunnerException e) {
            return ExecutableResult.exception(executable, e);
        }
    }

    public static ExecutableResult success(Executable target, ExecutableOutput executableOutput) {
        return new ExecutableResult(target, null, executableOutput);
    }

    public static ExecutableResult exception(Executable target, Exception exception) {
        return new ExecutableResult(target, exception, null);
    }

    public Optional<Exception> getException() {
        return Optional.ofNullable(exception);
    }

    public Optional<ExecutableOutput> getExecutableOutput() {
        return Optional.ofNullable(executableOutput);
    }

    public boolean isSuccessful() {
        return executableOutput != null && executableOutput.getReturnCode() == 0;
    }

    public List<String> getStandardOutputAsList() {
        if (executableOutput != null)
            return executableOutput.getStandardOutputAsList();

        return Collections.emptyList();
    }
}
