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
package com.synopsys.integration.detectable.detectables.clang;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommand;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommandDatabaseParser;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.ClangPackageDetailsTransformer;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.DependencyFileDetails;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.FilePathGenerator;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.PackageDetails;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManager;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerRunner;

public class ClangExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ExecutableRunner executableRunner;
    private final Gson gson;
    private final FileFinder fileFinder;
    private final FilePathGenerator filePathGenerator;
    private final ClangPackageDetailsTransformer clangPackageDetailsTransformer;

    public ClangExtractor(final ExecutableRunner executableRunner, final Gson gson, final FileFinder fileFinder, final FilePathGenerator filePathGenerator,
        final ClangPackageDetailsTransformer clangPackageDetailsTransformer) {
        this.executableRunner = executableRunner;
        this.gson = gson;
        this.fileFinder = fileFinder;
        this.filePathGenerator = filePathGenerator;
        this.clangPackageDetailsTransformer = clangPackageDetailsTransformer;
    }

    public Extraction extract(ClangPackageManager currentPackageManager, final ClangPackageManagerRunner packageManagerRunner, final File givenDir, final int depth, File outputDirectory, final File jsonCompilationDatabaseFile,
        boolean cleanup) {
        try {
            logger.info(String.format("Analyzing %s", jsonCompilationDatabaseFile.getAbsolutePath()));
            //final File rootDir = fileFinder.findContainingDir(givenDir, depth); //TODO: FIX
            final File rootDir = new File("");//TODO: FIX
            logger.debug(String.format("extract() called; compileCommandsJsonFilePath: %s", jsonCompilationDatabaseFile.getAbsolutePath()));
            final Set<File> unManagedDependencyFiles = ConcurrentHashMap.newKeySet(64);

            CompileCommandDatabaseParser databaseParser = new CompileCommandDatabaseParser(gson);
            final List<CompileCommand> compileCommands = databaseParser.parseCompileCommandDatabase(jsonCompilationDatabaseFile);

            final Set<File> filePaths = compileCommands.parallelStream()
                                            .flatMap(command -> filePathGenerator.fromCompileCommand(outputDirectory, command, cleanup).stream())
                                            .filter(StringUtils::isNotBlank)
                                            .map(File::new)
                                            .filter(File::exists)
                                            .collect(Collectors.toSet());
            
            logger.trace("Found : " + filePaths.size() + " files to process.");

            final Set<PackageDetails> packages = filePaths.parallelStream()
                                                     .map(file -> new DependencyFileDetails(true, file))//fileFinder.isFileUnderDir(rootDir, file)
                                                     .flatMap(detailedFile -> packageManagerRunner.getPackages(currentPackageManager, rootDir, executableRunner, unManagedDependencyFiles, detailedFile).stream())
                                                     .collect(Collectors.toSet());
            logger.trace("Found : " + packages.size() + " packages to process.");

            final CodeLocation codeLocation = clangPackageDetailsTransformer.toCodeLocation(currentPackageManager.getPackageManagerInfo().getDefaultForge(), currentPackageManager.getPackageManagerInfo().getForges(), rootDir, packages);
            logSummary(unManagedDependencyFiles);

            return new Extraction.Builder().success(codeLocation).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private void logSummary(final Set<File> unManagedDependencyFiles) {
        logger.info("Dependency files outside the build directory that were not recognized by the package manager:");
        for (final File unMatchedDependencyFile : unManagedDependencyFiles) {
            logger.info(String.format("\t%s", unMatchedDependencyFile.getAbsolutePath()));
        }

    }
}
