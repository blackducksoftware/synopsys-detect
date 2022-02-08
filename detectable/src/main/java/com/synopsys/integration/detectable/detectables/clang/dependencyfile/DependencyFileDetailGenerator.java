package com.synopsys.integration.detectable.detectables.clang.dependencyfile;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommand;

public class DependencyFileDetailGenerator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FilePathGenerator filePathGenerator;

    public DependencyFileDetailGenerator(FilePathGenerator filePathGenerator) {this.filePathGenerator = filePathGenerator;}

    public Set<File> fromCompileCommands(List<CompileCommand> compileCommands, File outputDirectory, boolean cleanup) {

        Set<File> dependencyFiles = compileCommands.parallelStream()
            .flatMap(command -> filePathGenerator.fromCompileCommand(outputDirectory, command, cleanup).stream())
            .filter(StringUtils::isNotBlank)
            .map(File::new)
            .filter(File::exists)
            .collect(Collectors.toSet());

        logger.trace("Found : " + dependencyFiles.size() + " files to process.");

        return dependencyFiles;
    }
}
