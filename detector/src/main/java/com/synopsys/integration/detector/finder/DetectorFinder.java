/*
 * detector
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detector.finder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;
import com.synopsys.integration.detector.rule.DetectorRuleSet;

public class DetectorFinder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Optional<DetectorEvaluationTree> findDetectors(File initialDirectory, DetectorRuleSet detectorRuleSet, DetectorFinderOptions options) throws DetectorFinderDirectoryListException {
        return findDetectors(initialDirectory, detectorRuleSet, 0, options);
    }

    private Optional<DetectorEvaluationTree> findDetectors(File directory, DetectorRuleSet detectorRuleSet, int depth, DetectorFinderOptions options)
        throws DetectorFinderDirectoryListException {

        if (depth > options.getMaximumDepth()) {
            logger.trace("Skipping directory as it exceeds max depth: " + directory.toString());
            return Optional.empty();
        }

        String directoryString = Optional.ofNullable(directory).map(File::toString).orElse("null");
        if (null == directory || !directory.isDirectory()) {
            logger.trace("Skipping file as it is not a directory: " + directoryString);
            return Optional.empty();
        }

        if (Files.isSymbolicLink(directory.toPath())) {
            if (!options.followSymLinks()) {
                logger.info("Skipping file as it is a symbolic link and following symbolic links has been disabled: " + directoryString);
                return Optional.empty();
            } else {
                logger.info("Following symbolic link: " + directoryString);
                Path linkTarget;
                try {
                    linkTarget = directory.toPath().toRealPath();
                } catch (IOException e) {
                    logger.debug("Symbolic link: " + directoryString + " does not point to a valid directory; skipping it");
                    return Optional.empty();
                }
                if (!Files.isDirectory(linkTarget)) {
                    logger.debug("Symbolic link: " + directoryString + " does not point to a valid directory; skipping it");
                    return Optional.empty();
                }
                directory = linkTarget.toFile();
            }
        }

        logger.info("Traversing directory: " + directory.getPath()); //TODO: Finding the perfect log level here is important. At INFO, we log a lot during a deep traversal but if we don't we might look stuck.
        // TODO why not do this only once?
        List<DetectorEvaluation> evaluations = detectorRuleSet.getOrderedDetectorRules().stream()
                                                   .map(DetectorEvaluation::new)
                                                   .collect(Collectors.toList());

        Set<DetectorEvaluationTree> children = new HashSet<>();

        List<File> subDirectories = findFilteredSubDirectories(directory, options.getFileFilter());
        logger.info("filteredSubDirectories: {}", subDirectories.toString());

        for (File subDirectory : subDirectories) {
            Optional<DetectorEvaluationTree> childEvaluationSet = findDetectors(subDirectory, detectorRuleSet, depth + 1, options);
            childEvaluationSet.ifPresent(children::add);
        }

        return Optional.of(new DetectorEvaluationTree(directory, depth, detectorRuleSet, evaluations, children));
    }

    private List<File> findFilteredSubDirectories(File directory, Predicate<File> filePredicate) throws DetectorFinderDirectoryListException {
        try (Stream<Path> pathStream = Files.list(directory.toPath())) {
            return pathStream.map(Path::toFile)
                       .filter(File::isDirectory)
                       .filter(filePredicate)
                       .collect(Collectors.toList());
        } catch (IOException e) {
            throw new DetectorFinderDirectoryListException(String.format("Could not get the subdirectories for %s. %s", directory.getAbsolutePath(), e.getMessage()), e);
        }
    }
}
