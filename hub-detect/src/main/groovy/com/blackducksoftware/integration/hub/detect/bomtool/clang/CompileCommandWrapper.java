package com.blackducksoftware.integration.hub.detect.bomtool.clang;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.util.Stringable;

public class CompileCommandWrapper extends Stringable {
    private final CompileCommand rawCompileCommand;

    public CompileCommandWrapper(final CompileCommand rawCompileCommand) {
        this.rawCompileCommand = rawCompileCommand;
    }

    public String getDirectory() {
        return rawCompileCommand.directory;
    }

    public String getFile() {
        return rawCompileCommand.file;
    }

    public String getCommand() {
        if (StringUtils.isNotBlank(rawCompileCommand.command)) {
            return rawCompileCommand.command;
        } else {
            return String.join(" ", rawCompileCommand.arguments);
        }

    }
}
