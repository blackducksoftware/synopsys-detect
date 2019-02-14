/**
 * synopsys-detect
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
package com.synopsys.integration.detect.detector.clang;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.util.executable.ExecutableRunner;
import com.synopsys.integration.detect.util.executable.ExecutableRunnerException;

public class DependenciesListFileManager {
    private static final String COMPILER_OUTPUT_FILE_OPTION = "-o";
    private static final String REPLACEMENT_OUTPUT_FILENAME = "/dev/null";
    private static final String DEPS_MK_FILENAME_PATTERN = "deps_%s_%d.mk";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final Random random = new Random();
    private final ExecutableRunner executableRunner;
    private final ClangCompileCommandParser compileCommandParser;

    public DependenciesListFileManager(final ExecutableRunner executableRunner, final ClangCompileCommandParser compileCommandParser) {
        this.executableRunner = executableRunner;
        this.compileCommandParser = compileCommandParser;
    }

    public Set<String> generateDependencyFilePaths(final File workingDir, final CompileCommand compileCommand, final boolean cleanup) {
        final Set<String> dependencyFilePaths = new HashSet<>();
        final Optional<File> depsMkFile = generate(workingDir, compileCommand);
        dependencyFilePaths.addAll(parse(depsMkFile.orElse(null)));
        if (cleanup) {
            depsMkFile.ifPresent(File::delete);
        }
        return dependencyFilePaths;
    }

    private Optional<File> generate(final File workingDir,
        final CompileCommand compileCommand) {
        final String depsMkFilename = deriveDependenciesListFilename(compileCommand);
        final File depsMkFile = new File(workingDir, depsMkFilename);
        Map<String, String> optionOverrides = new HashMap<>(1);
        optionOverrides.put(COMPILER_OUTPUT_FILE_OPTION, REPLACEMENT_OUTPUT_FILENAME);
        try {
            executableRunner.executeFromDirQuietly(new File(compileCommand.getDirectory()), compileCommandParser.getCompilerCommand(compileCommand.getCommand()),
                compileCommandParser.getCompilerArgsForGeneratingDepsMkFile(compileCommand.getCommand(), depsMkFile.getAbsolutePath(), optionOverrides));
        } catch (final ExecutableRunnerException e) {
            logger.debug(String.format("Error generating dependencies file for command '%s': %s", compileCommand.getCommand(), e.getMessage()));
            return Optional.empty();
        }
        return Optional.of(depsMkFile);
    }

    private List<String> parse(final File depsMkFile) {
        if (depsMkFile == null) {
            return new ArrayList<>(0);
        }
        List<String> dependencyFilePaths;
        try {
            final String depsDecl = FileUtils.readFileToString(depsMkFile, StandardCharsets.UTF_8);
            final String[] depsDeclParts = depsDecl.split(": ");
            if (depsDeclParts.length != 2) {
                logger.warn(String.format("Unable to parse %s contents: %s", depsMkFile.getAbsolutePath(), depsDecl));
                return new ArrayList<>(0);
            }
            String depsListString = depsDeclParts[1];
            logger.trace(String.format("dependencies: %s", depsListString));

            depsListString = depsListString.replaceAll("\n", " ");
            logger.trace(String.format("dependencies, newlines removed: %s", depsListString));

            depsListString = depsListString.replaceAll("\\\\", " ");
            logger.trace(String.format("dependencies, backslashes removed: %s", depsListString));

            final String[] deps = depsListString.split("\\s+");
            for (final String includeFile : deps) {
                logger.trace(String.format("\t%s", includeFile));
            }
            dependencyFilePaths = Arrays.asList(deps);
        } catch (final Exception e) {
            logger.warn(String.format("Error getting dependency file paths from '%s': %s", depsMkFile.getAbsolutePath(), e.getMessage()));
            return new ArrayList<>(0);
        }
        return dependencyFilePaths;
    }

    private String deriveDependenciesListFilename(final CompileCommand compileCommand) {
        final int randomInt = random.nextInt(1000);
        final String sourceFilenameBase = getFilenameBase(compileCommand.getFile());
        return String.format(DEPS_MK_FILENAME_PATTERN, sourceFilenameBase, randomInt);
    }

    private String getFilenameBase(final String filePathString) {
        final Path filePath = new File(filePathString).toPath();
        return FilenameUtils.removeExtension(filePath.getFileName().toString());
    }
}
