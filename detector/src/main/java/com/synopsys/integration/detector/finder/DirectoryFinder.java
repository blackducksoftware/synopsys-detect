package com.synopsys.integration.detector.finder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.common.util.finder.FileFinder;

public class DirectoryFinder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Optional<DirectoryFindResult> findDirectories(File initialDirectory, DirectoryFinderOptions options, FileFinder fileFinder) {
        logger.info("Searching for detectors.");
        return findDirectories(initialDirectory, 0, options, fileFinder);
    }

    private Optional<DirectoryFindResult> findDirectories(File directory, int depth, DirectoryFinderOptions options, FileFinder fileFinder) {

        if (depth > options.getMaximumDepth()) {
            logger.trace("Skipping directory as it exceeds max depth: {}", directory);
            return Optional.empty();
        }

        String directoryString = Optional.ofNullable(directory).map(File::toString).orElse("null");
        if (null == directory || !directory.isDirectory()) {
            logger.trace("Skipping file as it is not a directory: {}", directoryString);
            return Optional.empty();
        }

        if (Files.isSymbolicLink(directory.toPath())) {
            if (!options.followSymLinks()) {
                logger.debug("Skipping file as it is a symbolic link and following symbolic links has been disabled: {}", directoryString);
                return Optional.empty();
            } else {
                logger.debug("Following symbolic link: {}", directoryString);
                Path linkTarget;
                try {
                    linkTarget = directory.toPath().toRealPath();
                } catch (IOException e) {
                    logger.debug("Symbolic link: {} does not point to a valid directory; skipping it", directoryString);
                    return Optional.empty();
                }
                if (!linkTarget.toFile().isDirectory()) {
                    logger.debug("Symbolic link: {} does not point to a valid directory; skipping it", directoryString);
                    return Optional.empty();
                }
                directory = linkTarget.toFile();
            }
        }
        Set<DirectoryFindResult> children = new HashSet<>();

        List<File> subDirectories = fileFinder.findFiles(directory, options.getFileFilter());
        logger.debug("filteredSubDirectories: {}", subDirectories);

        for (File subDirectory : subDirectories) {
            Optional<DirectoryFindResult> subDirectoryResult = findDirectories(subDirectory, depth + 1, options, fileFinder);
            subDirectoryResult.ifPresent(children::add);
        }

        return Optional.of(new DirectoryFindResult(directory, depth, children));
    }
}
