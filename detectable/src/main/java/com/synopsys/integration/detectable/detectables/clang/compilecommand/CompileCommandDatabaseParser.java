package com.synopsys.integration.detectable.detectables.clang.compilecommand;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

public class CompileCommandDatabaseParser {

    final Gson gson;

    public CompileCommandDatabaseParser(Gson gson) {this.gson = gson;}

    public List<CompileCommand> parseCompileCommandDatabase(File compileCommandsDatabaseFile) throws IOException {
        String compileCommandsJson = FileUtils.readFileToString(compileCommandsDatabaseFile, StandardCharsets.UTF_8);
        CompileCommand[] compileCommands = gson.fromJson(compileCommandsJson, CompileCommand[].class);
        return Arrays.asList(compileCommands);
    }
}
