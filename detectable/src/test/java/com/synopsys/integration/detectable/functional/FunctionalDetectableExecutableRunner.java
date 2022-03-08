package com.synopsys.integration.detectable.functional;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.executable.Executable;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class FunctionalDetectableExecutableRunner implements DetectableExecutableRunner {
    private final Map<FunctionalExecutable, ExecutableOutput> executableExecutableOutputMap = new HashMap<>();

    public void addExecutableOutput(@NotNull Executable executable, @NotNull ExecutableOutput executableOutput) {
        executableExecutableOutputMap.put(new FunctionalExecutable(executable), executableOutput);
    }

    @NotNull
    @Override
    public ExecutableOutput execute(File workingDirectory, List<String> command) throws ExecutableRunnerException {
        return execute(new Executable(workingDirectory, new HashMap<>(), command));
    }

    @NotNull
    @Override
    public ExecutableOutput execute(@NotNull File workingDirectory, @NotNull String exeCmd, @NotNull String... args) {
        return execute(workingDirectory, new File(exeCmd), args);
    }

    @NotNull
    @Override
    public ExecutableOutput execute(@NotNull File workingDirectory, @NotNull String exeCmd, @NotNull List<String> args) {
        return execute(workingDirectory, new File(exeCmd), args);
    }

    @NotNull
    @Override
    public ExecutableOutput execute(@NotNull File workingDirectory, @NotNull File exeFile, @NotNull String... args) {
        return execute(workingDirectory, exeFile, Arrays.asList(args));
    }

    @NotNull
    @Override
    public ExecutableOutput execute(@NotNull File workingDirectory, @NotNull File exeFile, @NotNull List<String> args) {

        List<String> command = new ArrayList<>();
        command.add(exeFile.getPath());
        command.addAll(args);

        return execute(new Executable(workingDirectory, new HashMap<>(), command));
    }

    @NotNull
    @Override
    public ExecutableOutput executeSecretly(Executable executable) throws ExecutableRunnerException {
        return execute(executable);//Functional tests don't care about 'secret' executions (where secret means the output might contain something secret like credentials).
    }

    @NotNull
    @Override
    public ExecutableOutput execute(@NotNull Executable executable) {

        ExecutableOutput executableOutput = executableExecutableOutputMap.get(new FunctionalExecutable(executable));
        if (executableOutput == null) {
            StringBuilder errorMessage = new StringBuilder("Missing mocked executable output for:")
                .append(System.lineSeparator())
                .append(executable.getExecutableDescription())
                .append(System.lineSeparator());

            errorMessage.append("Executable Output Map Contents:").append(System.lineSeparator());
            executableExecutableOutputMap.forEach((key, value) -> {
                errorMessage.append("--------------------------------").append(System.lineSeparator());

                errorMessage.append("Key: ")
                    .append(System.lineSeparator())
                    .append(key.getReferencedExecutable().getExecutableDescription())
                    .append(System.lineSeparator());
                errorMessage.append("Standard Output: ")
                    .append(System.lineSeparator())
                    .append(value.getStandardOutput())
                    .append(System.lineSeparator());
                errorMessage.append("Error Output: ")
                    .append(System.lineSeparator())
                    .append(value.getErrorOutput())
                    .append(System.lineSeparator());
                errorMessage.append("Return Code: ")
                    .append(System.lineSeparator())
                    .append(value.getReturnCode())
                    .append(System.lineSeparator());
                errorMessage.append(System.lineSeparator());
            });

            throw new RuntimeException(errorMessage.toString());
        }

        return executableOutput;
    }

    @Override
    @NotNull
    public ExecutableOutput executeSuccessfully(Executable executable) throws ExecutableFailedException {
        ExecutableOutput output = execute(executable);
        if (output.getReturnCode() != 0)
            throw new ExecutableFailedException(executable, output);

        return output;
    }
}
