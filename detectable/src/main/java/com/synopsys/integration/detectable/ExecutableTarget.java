package com.synopsys.integration.detectable;

import java.io.File;

import org.jetbrains.annotations.Nullable;

public class ExecutableTarget {
    private @Nullable
    File fileTarget;
    private @Nullable
    String stringTarget;

    private ExecutableTarget(@Nullable File fileTarget, @Nullable String stringTarget) {
        this.fileTarget = fileTarget;
        this.stringTarget = stringTarget;
    }

    @Nullable
    public static ExecutableTarget forFile(@Nullable File targetFile) { //For example "C:\bin\git.exe"
        if (targetFile == null)
            return null;
        return new ExecutableTarget(targetFile, null);
    }

    @Nullable
    public static ExecutableTarget forCommand(@Nullable String command) { //For example "git".
        if (command == null)
            return null;
        return new ExecutableTarget(null, command);
    }

    @Nullable
    public String toCommand() {
        if (stringTarget != null) {
            return stringTarget;
        }

        if (fileTarget != null) {
            return fileTarget.getAbsolutePath();
        }

        return null;
    }
}
