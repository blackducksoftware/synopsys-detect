package com.synopsys.integration.detectable.detectables.conan.cli.process;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class ConanCommandRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DetectableExecutableRunner executableRunner;
    @Nullable
    private final Path lockfilePath;
    @Nullable
    private final String additionalArguments;

    public ConanCommandRunner(DetectableExecutableRunner executableRunner, @Nullable Path lockfilePath, @Nullable String additionalArguments) {
        this.executableRunner = executableRunner;
        this.lockfilePath = lockfilePath;
        this.additionalArguments = additionalArguments;
    }

    public ExecutableOutput runConanInfoCommand(File projectDir, ExecutableTarget conanExe) throws ExecutableRunnerException {
        List<String> conanInfoArguments = generateConanInfoCommandArguments(projectDir);
        return executableRunner.execute(ExecutableUtils.createFromTarget(projectDir, conanExe, conanInfoArguments));
    }

    public boolean wasSuccess(ExecutableOutput conanInfoOutput) {
        String errorOutput = conanInfoOutput.getErrorOutput();
        if (StringUtils.isNotBlank(errorOutput) && errorOutput.contains("ERROR: ")) {
            logger.error("The conan info command reported errors: {}", errorOutput);
            return false;
        }
        if (StringUtils.isNotBlank(errorOutput)) {
            logger.debug("The conan info command wrote to stderr: {}", errorOutput);
        }
        return true;
    }

    public boolean producedOutput(ExecutableOutput conanInfoOutput) {
        String standardOutput = conanInfoOutput.getStandardOutput();
        if (StringUtils.isBlank(standardOutput)) {
            logger.error("Nothing returned from conan info command");
            return false;
        }
        return true;
    }

    @NotNull
    private List<String> generateConanInfoCommandArguments(File projectDir) {
        List<String> exeArgs = new ArrayList<>();
        exeArgs.add("info");
        if (lockfilePath != null) {
            exeArgs.add("--lockfile");
            exeArgs.add(lockfilePath.toString());
        }
        if (StringUtils.isNotEmpty(additionalArguments)) {
            String[] additionalArgs = additionalArguments.split(" +");
            exeArgs.addAll(Arrays.asList(additionalArgs));
        }
        exeArgs.add(projectDir.getAbsolutePath());
        return exeArgs;
    }
}
