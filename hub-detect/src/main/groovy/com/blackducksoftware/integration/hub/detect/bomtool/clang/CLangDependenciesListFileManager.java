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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

public class CLangDependenciesListFileManager {
    public static final String DEPS_MK_FILENAME_PATTERN = "deps_%s_%d.mk";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExecutableRunner executableRunner;

    public CLangDependenciesListFileManager(final ExecutableRunner executableRunner) {
        this.executableRunner = executableRunner;
    }

    public Optional<File> generate(final File workingDir,
            final CLangCompileCommand compileCommand) {

        final String depsMkFilename = deriveDependenciesListFilename(compileCommand);
        final File depsMkFile = new File(workingDir, depsMkFilename);
        try {
            executableRunner.executeFromDirQuietly(new File(compileCommand.directory), getCompilerCommand(compileCommand.command),
                    getCompilerArgsForGeneratingDepsMkFile(compileCommand.command, depsMkFile.getAbsolutePath()));
        } catch (final ExecutableRunnerException e) {
            logger.debug(String.format("Error generating dependencies file for command '%s': %s", compileCommand.command, e.getMessage()));
            return Optional.empty();
        }
        return Optional.of(depsMkFile);
    }

    public List<String> parse(final File depsMkFile) {
        if (depsMkFile == null) {
            return new ArrayList<>(0);
        }
        List<String> dependencyFilePaths;
        try {
            final String depsDecl = FileUtils.readFileToString(depsMkFile, StandardCharsets.UTF_8);
            final String[] depsDeclParts = depsDecl.split(": ");
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
        } catch (final IOException e) {
            logger.warn(String.format("Error getting dependency file paths from '%s': %s", depsMkFile.getAbsolutePath(), e.getMessage()));
            return new ArrayList<>(0);
        }
        return dependencyFilePaths;
    }

    private String deriveDependenciesListFilename(final CLangCompileCommand compileCommand) {
        final int randomInt = (int) (Math.random() * 1000);
        final String sourceFilenameBase = getFilenameBase(compileCommand.file);
        final String depsMkFilename = String.format(DEPS_MK_FILENAME_PATTERN, sourceFilenameBase, randomInt);
        return depsMkFilename;
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
                argList.add(part);
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
