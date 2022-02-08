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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommand;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommandParser;
import com.synopsys.integration.executable.Executable;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class FilePathGenerator {
    private static final String COMPILER_OUTPUT_FILE_OPTION = "-o";
    private static final String REPLACEMENT_OUTPUT_FILENAME = "/dev/null";
    private static final String DEPS_MK_FILENAME_PATTERN = "deps_%s_%d.mk";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final Random random = new Random();
    private final DetectableExecutableRunner executableRunner;
    private final CompileCommandParser commandParser;
    private final DependencyListFileParser dependencyListFileParser;

    public FilePathGenerator(DetectableExecutableRunner executableRunner, CompileCommandParser commandParser, DependencyListFileParser dependencyListFileParser) {
        this.executableRunner = executableRunner;
        this.commandParser = commandParser;
        this.dependencyListFileParser = dependencyListFileParser;
    }

    public List<String> fromCompileCommand(File workingDir, CompileCommand compileCommand, boolean cleanup) {
        Optional<File> depsMkFile = generateDepsMkFile(workingDir, compileCommand);
        if (depsMkFile.isPresent()) {
            List<String> files = dependencyListFileParser.parseDepsMk(depsMkFile.get());
            if (cleanup) {
                FileUtils.deleteQuietly(depsMkFile.get());
            }
            return files;
        } else {
            return Collections.emptyList();
        }
    }

    private Optional<File> generateDepsMkFile(File workingDir, CompileCommand compileCommand) {
        String depsMkFilename = deriveDependenciesListFilename(compileCommand);
        File depsMkFile = new File(workingDir, depsMkFilename);
        Map<String, String> optionOverrides = new HashMap<>(1);
        optionOverrides.put(COMPILER_OUTPUT_FILE_OPTION, REPLACEMENT_OUTPUT_FILENAME);
        try {
            List<String> command = commandParser.parseCommand(compileCommand, optionOverrides);
            command.addAll(Arrays.asList("-M", "-MF", depsMkFile.getAbsolutePath()));
            Executable executable = Executable.create(new File(compileCommand.getDirectory()), Collections.emptyMap(), command);
            executableRunner.execute(executable);
        } catch (ExecutableRunnerException e) {
            logger.debug(String.format("Error generating dependencies file for command '%s': %s", compileCommand.getCommand(), e.getMessage()));
            return Optional.empty();
        }
        return Optional.of(depsMkFile);
    }

    private String deriveDependenciesListFilename(CompileCommand compileCommand) {
        int randomInt = random.nextInt(1000);
        String sourceFilenameBase = getFilenameBase(compileCommand.getFile());
        return String.format(DEPS_MK_FILENAME_PATTERN, sourceFilenameBase, randomInt);
    }

    private String getFilenameBase(String filePathString) {
        Path filePath = new File(filePathString).toPath();
        return FilenameUtils.removeExtension(filePath.getFileName().toString());
    }
}
