/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.bomtool.clang;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

public class DependenciesListFileManager {
    private static final String REPLACEMENT_OUTPUT_FILENAME = "/dev/null";
    private static final String COMPILER_OUTPUT_FILE_OPTION = "-o";
    public static final String DEPS_MK_FILENAME_PATTERN = "deps_%s_%d.mk";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Random random = new Random(new Date().getTime());
    private final ExecutableRunner executableRunner;

    public DependenciesListFileManager(final ExecutableRunner executableRunner) {
        this.executableRunner = executableRunner;
    }

    public Set<String> generateDependencyFilePaths(final File workingDir, final CompileCommandWrapper compileCommand) {
        final Set<String> dependencyFilePaths = new HashSet<>();
        final Optional<File> depsMkFile = generate(workingDir, compileCommand);
        dependencyFilePaths.addAll(parse(depsMkFile.orElse(null)));
        depsMkFile.ifPresent(File::delete);
        return dependencyFilePaths;
    }

    private Optional<File> generate(final File workingDir,
            final CompileCommandWrapper compileCommand) {
        final String depsMkFilename = deriveDependenciesListFilename(compileCommand);
        final File depsMkFile = new File(workingDir, depsMkFilename);
        try {
            executableRunner.executeFromDirQuietly(new File(compileCommand.getDirectory()), getCompilerCommand(compileCommand.getCommand()),
                    getCompilerArgsForGeneratingDepsMkFile(compileCommand.getCommand(), depsMkFile.getAbsolutePath()));
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

    private String deriveDependenciesListFilename(final CompileCommandWrapper compileCommand) {
        final int randomInt = random.nextInt(1) * 1000;
        final String sourceFilenameBase = getFilenameBase(compileCommand.getFile());
        return String.format(DEPS_MK_FILENAME_PATTERN, sourceFilenameBase, randomInt);
    }

    private String getCompilerCommand(final String origCompileCommand) {
        final String[] parts = origCompileCommand.trim().split("\\s+");
        return parts[0];
    }

    private List<String> getCompilerArgsForGeneratingDepsMkFile(final String origCompileCommand, final String depsMkFilePath) {
        final String[] parts = origCompileCommand.trim().split("\\s+");
        final List<String> argList = new ArrayList<>(parts.length + 3);
        int partIndex = 0;
        for (final String part : parts) {
            if (partIndex > 0) {
                if (COMPILER_OUTPUT_FILE_OPTION.equals(parts[partIndex - 1])) {
                    logger.trace(String.format("Replacing compiler output file %s with %s", part, REPLACEMENT_OUTPUT_FILENAME));
                    argList.add(REPLACEMENT_OUTPUT_FILENAME);
                } else {
                    argList.add(part);
                }
            }
            partIndex++;
        }
        argList.add("-M");
        argList.add("-MF");
        argList.add(depsMkFilePath);
        return argList;
    }

    private String getFilenameBase(final String filePathString) {
        final Path filePath = new File(filePathString).toPath();
        return FilenameUtils.removeExtension(filePath.getFileName().toString());
    }
}
