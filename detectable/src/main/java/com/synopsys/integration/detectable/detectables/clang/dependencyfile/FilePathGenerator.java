/**
 * hub-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detectable.detectables.clang.dependencyfile;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommand;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommandParser;

public class FilePathGenerator {
    private static final String COMPILER_OUTPUT_FILE_OPTION = "-o";
    private static final String REPLACEMENT_OUTPUT_FILENAME = "/dev/null";
    private static final String DEPS_MK_FILENAME_PATTERN = "deps_%s_%d.mk";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final Random random = new Random();
    private final ExecutableRunner executableRunner;
    private final CompileCommandParser compileCommandParser;
    private final DependenyListFileParser dependenyListFileParser;

    public FilePathGenerator(final ExecutableRunner executableRunner, final CompileCommandParser compileCommandParser, final DependenyListFileParser dependenyListFileParser) {
        this.executableRunner = executableRunner;
        this.compileCommandParser = compileCommandParser;
        this.dependenyListFileParser = dependenyListFileParser;
    }

    public List<String> fromCompileCommand(final File workingDir, final CompileCommand compileCommand, final boolean cleanup) {
        final Optional<File> depsMkFile = generateDepsMkFile(workingDir, compileCommand);
        if (depsMkFile.isPresent()) {
            List<String> files = dependenyListFileParser.parseDepsMk(depsMkFile.get());
            if (cleanup) {
                depsMkFile.get().delete();
            }
            return files;
        } else {
            return Collections.emptyList();
        }
    }

    private Optional<File> generateDepsMkFile(final File workingDir, final CompileCommand compileCommand) {
        final String depsMkFilename = deriveDependenciesListFilename(compileCommand);
        final File depsMkFile = new File(workingDir, depsMkFilename);
        Map<String, String> optionOverrides = new HashMap<>(1);
        optionOverrides.put(COMPILER_OUTPUT_FILE_OPTION, REPLACEMENT_OUTPUT_FILENAME);
        try {
            File compileCommandDirectory = new File(compileCommand.directory);
            String command = compileCommandParser.parseActualCommand(compileCommand);
            List<String> args = compileCommandParser.parseArguments(compileCommand, optionOverrides);
            args.addAll(Arrays.asList("-M", "-MF", depsMkFile.getAbsolutePath()));
            executableRunner.execute(compileCommandDirectory, command, args);
        } catch (final ExecutableRunnerException e) {
            logger.debug(String.format("Error generating dependencies file for command '%s': %s", compileCommand.command, e.getMessage()));
            return Optional.empty();
        }
        return Optional.of(depsMkFile);
    }

    private String deriveDependenciesListFilename(final CompileCommand compileCommand) {
        final int randomInt = random.nextInt(1000);
        final String sourceFilenameBase = getFilenameBase(compileCommand.file);
        return String.format(DEPS_MK_FILENAME_PATTERN, sourceFilenameBase, randomInt);
    }

    private String getFilenameBase(final String filePathString) {
        final Path filePath = new File(filePathString).toPath();
        return FilenameUtils.removeExtension(filePath.getFileName().toString());
    }
}
