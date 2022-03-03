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
    private final ForgeChooser forgeChooser;

    public ClangExtractor(
        DetectableExecutableRunner executableRunner,
        DependencyFileDetailGenerator dependencyFileDetailGenerator,
        ClangPackageDetailsTransformer clangPackageDetailsTransformer,
        CompileCommandDatabaseParser compileCommandDatabaseParser,
        ForgeChooser forgeChooser
    ) {
        this.executableRunner = executableRunner;
        this.dependencyFileDetailGenerator = dependencyFileDetailGenerator;
        this.clangPackageDetailsTransformer = clangPackageDetailsTransformer;
        this.compileCommandDatabaseParser = compileCommandDatabaseParser;
        this.forgeChooser = forgeChooser;
    }

    public Extraction extract(
        ClangPackageManager currentPackageManager,
        ClangPackageManagerRunner packageManagerRunner,
        File sourceDirectory,
        File outputDirectory,
        File jsonCompilationDatabaseFile,
        boolean cleanup
    ) {
        try {
            logger.debug(String.format("Analyzing %s", jsonCompilationDatabaseFile.getAbsolutePath()));
            logger.debug(String.format("extract() called; compileCommandsJsonFilePath: %s", jsonCompilationDatabaseFile.getAbsolutePath()));

            List<CompileCommand> compileCommands = compileCommandDatabaseParser.parseCompileCommandDatabase(jsonCompilationDatabaseFile);
            Set<File> dependencyFileDetails = dependencyFileDetailGenerator.fromCompileCommands(compileCommands, outputDirectory, cleanup);
            PackageDetailsResult results = packageManagerRunner.getAllPackages(currentPackageManager, sourceDirectory, executableRunner, dependencyFileDetails);

            logger.trace("Found : " + results.getFoundPackages() + " packages.");
            logger.trace("Found : " + results.getUnRecognizedDependencyFiles() + " non-package files.");

            List<Forge> packageForges = forgeChooser.determineForges(currentPackageManager);
            CodeLocation codeLocation = clangPackageDetailsTransformer.toCodeLocation(packageForges, results.getFoundPackages());

            logFileCollection("Unrecognized dependency files (all)", results.getUnRecognizedDependencyFiles());
            List<File> unrecognizedIncludeFiles = results.getUnRecognizedDependencyFiles().stream()
                .filter(file -> !isFileUnderDir(sourceDirectory, file))
                .collect(Collectors.toList());
            logFileCollection(
                String.format("Unrecognized dependency files that are outside the compile_commands.json directory (%s) and will be collected", sourceDirectory),
                unrecognizedIncludeFiles
            );

            return new Extraction.Builder()
                .unrecognizedPaths(unrecognizedIncludeFiles)
                .success(codeLocation).build();
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    public boolean isFileUnderDir(File dir, File file) {
        try {
            String dirPath = dir.getCanonicalPath();
            String filePath = file.getCanonicalPath();
            if (filePath.startsWith(dirPath)) {
                return true;
            }
            return false;
        } catch (IOException e) {
            logger.warn(String.format("Error getting canonical path for either %s or %s", dir.getAbsolutePath(), file.getAbsolutePath()));
            return false;
        }
    }

    private void logFileCollection(String description, Collection<File> files) {
        if (files == null) {
            files = new ArrayList<>(0);
        }
        logger.debug(String.format("%s (%d files):", description, files.size()));
        for (File file : files) {
            logger.debug(String.format("\t%s", file.getAbsolutePath()));
        }
    }
}
