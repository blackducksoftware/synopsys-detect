package com.synopsys.integration.detectable.detectables.conan.cli.process;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConanArgumentGenerator {
    @Nullable
    private final Path lockfilePath;
    @Nullable
    private final String additionalArguments;

    public ConanArgumentGenerator(@Nullable Path lockfilePath, @Nullable String additionalArguments) {
        this.lockfilePath = lockfilePath;
        this.additionalArguments = additionalArguments;
    }

    @NotNull
    public List<String> generateConanInfoCmdArgs(File projectDir) {
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
