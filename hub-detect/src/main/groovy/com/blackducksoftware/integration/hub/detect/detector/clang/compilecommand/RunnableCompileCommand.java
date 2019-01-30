package com.blackducksoftware.integration.hub.detect.detector.clang.compilecommand;

import java.io.File;

public class RunnableCompileCommand {
    private final File directory;
    private final String command;

    public RunnableCompileCommand(File directory, String command) {
        this.directory = directory;
        this.command = command;
    }

    public File getDirectory() {
        return directory;
    }

    public String getCommand() {
        return command;
    }
}
