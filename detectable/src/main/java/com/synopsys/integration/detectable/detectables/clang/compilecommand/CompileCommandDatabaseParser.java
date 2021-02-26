/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
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

    public CompileCommandDatabaseParser(final Gson gson) {this.gson = gson;}

    public List<CompileCommand> parseCompileCommandDatabase(final File compileCommandsDatabaseFile) throws IOException {
        final String compileCommandsJson = FileUtils.readFileToString(compileCommandsDatabaseFile, StandardCharsets.UTF_8);
        final CompileCommand[] compileCommands = gson.fromJson(compileCommandsJson, CompileCommand[].class);
        return Arrays.asList(compileCommands);
    }
}
