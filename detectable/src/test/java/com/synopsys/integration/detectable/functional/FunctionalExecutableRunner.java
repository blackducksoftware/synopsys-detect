package com.synopsys.integration.detectable.functional;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.detectable.detectable.executable.Executable;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;

public class FunctionalExecutableRunner implements ExecutableRunner {
    private final Map<Executable, ExecutableOutput> executableExecutableOutputMap = new HashMap<>();

    public void addExecutableOutput(@NotNull final Executable executable, @NotNull final ExecutableOutput executableOutput) {
        executableExecutableOutputMap.put(executable, executableOutput);
    }

    @Override
    public ExecutableOutput execute(@NotNull final File workingDirectory, @NotNull final String exeCmd, @NotNull final String... args) {
        return execute(workingDirectory, new File(exeCmd), args);
    }

    @Override
    public ExecutableOutput execute(@NotNull final File workingDirectory, @NotNull final String exeCmd, @NotNull final List<String> args) {
        return execute(workingDirectory, new File(exeCmd), args);
    }

    @Override
    public ExecutableOutput execute(@NotNull final File workingDirectory, @NotNull final File exeFile, @NotNull final String... args) {
        return execute(workingDirectory, exeFile, Arrays.asList(args));
    }

    @Override
    public ExecutableOutput execute(@NotNull final File workingDirectory, @NotNull final File exeFile, @NotNull final List<String> args) {
        final List<String> command = new ArrayList<>();
        command.add(exeFile.getPath());
        command.addAll(args);

        return execute(new Executable(workingDirectory, new HashMap<>(), command));
    }

    @Override
    public ExecutableOutput execute(@NotNull final Executable executable) {
        return executableExecutableOutputMap.get(executable);
    }
}
