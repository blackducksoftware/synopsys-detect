/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.clang;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommand;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommandDatabaseParser;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.ClangPackageDetailsTransformer;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.DependencyFileDetailGenerator;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManager;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerRunner;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.PackageDetailsResult;

public class ClangExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ExecutableRunner executableRunner;
    private final DependencyFileDetailGenerator dependencyFileDetailGenerator;
    private final ClangPackageDetailsTransformer clangPackageDetailsTransformer;
    private final CompileCommandDatabaseParser compileCommandDatabaseParser;

    public ClangExtractor(final ExecutableRunner executableRunner, final DependencyFileDetailGenerator dependencyFileDetailGenerator,
        final ClangPackageDetailsTransformer clangPackageDetailsTransformer, final CompileCommandDatabaseParser compileCommandDatabaseParser) {
        this.executableRunner = executableRunner;
        this.dependencyFileDetailGenerator = dependencyFileDetailGenerator;
        this.clangPackageDetailsTransformer = clangPackageDetailsTransformer;
        this.compileCommandDatabaseParser = compileCommandDatabaseParser;
    }

    public Extraction extract(final ClangPackageManager currentPackageManager, final ClangPackageManagerRunner packageManagerRunner, final File sourceDirectory, final File outputDirectory, final File jsonCompilationDatabaseFile,
        final boolean cleanup) {
        try {
            logger.debug(String.format("Analyzing %s", jsonCompilationDatabaseFile.getAbsolutePath()));
            logger.debug(String.format("extract() called; compileCommandsJsonFilePath: %s", jsonCompilationDatabaseFile.getAbsolutePath()));

            final List<CompileCommand> compileCommands = compileCommandDatabaseParser.parseCompileCommandDatabase(jsonCompilationDatabaseFile);
            final Set<File> dependencyFileDetails = dependencyFileDetailGenerator.fromCompileCommands(compileCommands, outputDirectory, cleanup);
            final PackageDetailsResult results = packageManagerRunner.getAllPackages(currentPackageManager, sourceDirectory, executableRunner, dependencyFileDetails);

            logger.trace("Found : " + results.getFoundPackages() + " packages.");
            logger.trace("Found : " + results.getUnRecognizedDependencyFiles() + " non-package files.");

            final List<Forge> packageForges = currentPackageManager.getPackageManagerInfo().getForges();
            final CodeLocation codeLocation = clangPackageDetailsTransformer.toCodeLocation(packageForges, results.getFoundPackages());

            logSummary(results.getUnRecognizedDependencyFiles(), sourceDirectory);

            // TEMP: fake a list of unrecognized include files
            final List<File> unrecognizedIncludeFiles = results.getUnRecognizedDependencyFiles().stream()
                                                            .filter(file -> isFileUnderDir(sourceDirectory, file))
                                                            .collect(Collectors.toList());

            return new Extraction.Builder()
                       .unrecognizedPaths(unrecognizedIncludeFiles)
                       .success(codeLocation).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    public boolean isFileUnderDir(final File dir, final File file) {
        try {
            final String dirPath = dir.getCanonicalPath();
            final String filePath = file.getCanonicalPath();
            if (filePath.startsWith(dirPath)) {
                return true;
            }
            return false;
        } catch (final IOException e) {
            logger.warn(String.format("Error getting canonical path for either %s or %s", dir.getAbsolutePath(), file.getAbsolutePath()));
            return false;
        }
    }

    private void logSummary(final Set<File> unRecognizedDependencyFiles, final File sourceDirectory) {
        logger.debug("Dependency files outside the build directory that were not recognized by the package manager:");
        for (final File unRecognizedDependencyFile : unRecognizedDependencyFiles) {
            try {
                if (FileUtils.directoryContains(sourceDirectory, unRecognizedDependencyFile)) {
                    logger.debug(String.format("\t%s is not managed, but it's in the source.dir, ignoring.", unRecognizedDependencyFile.getAbsolutePath()));
                } else {
                    logger.debug(String.format("\t%s", unRecognizedDependencyFile.getAbsolutePath()));
                }
            } catch (final IOException e) {
                logger.debug(String.format("%s may or may not be in the source dir (attempt to verify location failed).", unRecognizedDependencyFile.getAbsolutePath()));
                logger.debug(String.format("\t%s", unRecognizedDependencyFile.getAbsolutePath()));
            }
        }
    }
}
