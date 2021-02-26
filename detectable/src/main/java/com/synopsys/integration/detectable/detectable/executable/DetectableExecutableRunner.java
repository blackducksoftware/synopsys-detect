/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.executable;

import java.io.File;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.executable.Executable;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

//This is essentially temporary as we migrate to Integration commons executable runner and allow detectables to throw exceptions.
public interface DetectableExecutableRunner {
    @NotNull
    ExecutableOutput execute(final File workingDirectory, List<String> command) throws ExecutableRunnerException;

    @NotNull
    ExecutableOutput execute(final File workingDirectory, final String exeCmd, final String... args) throws ExecutableRunnerException;

    @NotNull
    ExecutableOutput execute(final File workingDirectory, final String exeCmd, final List<String> args) throws ExecutableRunnerException;

    @NotNull
    ExecutableOutput execute(final File workingDirectory, final File exeFile, final String... args) throws ExecutableRunnerException;

    @NotNull
    ExecutableOutput execute(final File workingDirectory, final File exeFile, final List<String> args) throws ExecutableRunnerException;

    @NotNull
    ExecutableOutput execute(Executable executable) throws ExecutableRunnerException;

    @NotNull
    ExecutableOutput executeSecretly(Executable executable) throws ExecutableRunnerException;

    @NotNull
    ExecutableOutput executeSuccessfully(Executable executable) throws ExecutableFailedException; //Returns output if and only if executable return code was zero, otherwise throws.
}
