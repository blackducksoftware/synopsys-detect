/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.clang;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommand;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommandDatabaseParser;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.ClangPackageDetailsTransformer;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.DependencyFileDetailGenerator;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManager;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerRunner;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.PackageDetailsResult;
import com.synopsys.integration.detectable.extraction.Extraction;

public class ClangExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DetectableExecutableRunner executableRunner;
    private final DependencyFileDetailGenerator dependencyFileDetailGenerator;
    private final ClangPackageDetailsTransformer clangPackageDetailsTransformer;
    private final CompileCommandDatabaseParser compileCommandDatabaseParser;

    public ClangExtractor(final DetectableExecutableRunner executableRunner, final DependencyFileDetailGenerator dependencyFileDetailGenerator,
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

            logFileCollection("Unrecognized dependency files (all)", results.getUnRecognizedDependencyFiles());
            final List<File> unrecognizedIncludeFiles = results.getUnRecognizedDependencyFiles().stream()
                                                            .filter(file -> !isFileUnderDir(sourceDirectory, file))
                                                            .collect(Collectors.toList());
            logFileCollection(String.format("Unrecognized dependency files that are outside the compile_commands.json directory (%s) and will be collected", sourceDirectory), unrecognizedIncludeFiles);

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

    private void logFileCollection(final String description, Collection<File> files) {
        if (files == null) {
            files = new ArrayList<>(0);
        }
        logger.debug(String.format("%s (%d files):", description, files.size()));
        for (final File file : files) {
            logger.debug(String.format("\t%s", file.getAbsolutePath()));
        }
    }
}
