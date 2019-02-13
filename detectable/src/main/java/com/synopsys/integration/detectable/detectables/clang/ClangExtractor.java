/**
 * detectable
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.file.FileUtils;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommand;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommandDatabaseParser;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.ClangPackageDetailsTransformer;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.DependencyFileDetailGenerator;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.DependencyFileDetails;
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
        final ClangPackageDetailsTransformer clangPackageDetailsTransformer, CompileCommandDatabaseParser compileCommandDatabaseParser) {
        this.executableRunner = executableRunner;
        this.dependencyFileDetailGenerator = dependencyFileDetailGenerator;
        this.clangPackageDetailsTransformer = clangPackageDetailsTransformer;
        this.compileCommandDatabaseParser = compileCommandDatabaseParser;
    }

    public Extraction extract(ClangPackageManager currentPackageManager, final ClangPackageManagerRunner packageManagerRunner, final File givenDir, final int depth, File outputDirectory, final File jsonCompilationDatabaseFile,
        boolean cleanup) {
        try {
            logger.info(String.format("Analyzing %s", jsonCompilationDatabaseFile.getAbsolutePath()));
            final File rootDir = FileUtils.getParent(givenDir, depth);//TODO: Make sure we need this
            logger.debug(String.format("extract() called; compileCommandsJsonFilePath: %s", jsonCompilationDatabaseFile.getAbsolutePath()));

            final List<CompileCommand> compileCommands = compileCommandDatabaseParser.parseCompileCommandDatabase(jsonCompilationDatabaseFile);
            final Set<DependencyFileDetails> dependencyFileDetails = dependencyFileDetailGenerator.fromCompileCommands(compileCommands, rootDir, outputDirectory, cleanup);
            final PackageDetailsResult results = packageManagerRunner.getAllPackages(currentPackageManager, rootDir, executableRunner, dependencyFileDetails);

            logger.trace("Found : " + results.getFoundPackages() + " packages.");
            logger.trace("Found : " + results.getUnmanagedDependencies() + " unmanaged files.");

            Forge defaultForge = currentPackageManager.getPackageManagerInfo().getDefaultForge();
            List<Forge> packageForges = currentPackageManager.getPackageManagerInfo().getForges();
            final CodeLocation codeLocation = clangPackageDetailsTransformer.toCodeLocation(defaultForge, packageForges, rootDir, results.getFoundPackages());

            logSummary(results.getUnmanagedDependencies());

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
