package com.synopsys.integration.detectable.detectables.clang.compilecommand;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.util.Stringable;

// Loaded from json via Gson
public class CompileCommand extends Stringable {
    @SerializedName("directory")
    private String directory = "";
    @SerializedName("command")
    private String command = "";
    @SerializedName("arguments")
    private String[] arguments = new String[] {};
    @SerializedName("file")
    private String file = "";

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String[] getArguments() {
        return arguments;
    }

    public void setArguments(String[] arguments) {
        this.arguments = arguments;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
