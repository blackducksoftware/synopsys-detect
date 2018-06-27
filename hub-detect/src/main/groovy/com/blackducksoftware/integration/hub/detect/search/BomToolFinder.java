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
package com.blackducksoftware.integration.hub.detect.search;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolFactory;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolSearchRuleSet;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.evaluation.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.extraction.model.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.model.BomToolGroupType;

public class BomToolFinder {
    private final Logger logger = LoggerFactory.getLogger(BomToolFinder.class);

    public List<BomToolEvaluation> findApplicableBomTools(final BomToolFactory bomToolFactory, final File initialDirectory, final BomToolFinderOptions options) throws BomToolException, DetectUserFriendlyException {
        final List<File> subDirectories = new ArrayList<>();
        subDirectories.add(initialDirectory);
        return findApplicableBomTools(bomToolFactory, subDirectories, new HashSet<BomToolType>(), 0, options);
    }

    private List<BomToolEvaluation> findApplicableBomTools(final BomToolFactory bomToolFactory, final List<File> directoriesToSearch, final Set<BomToolType> appliedBefore, final int depth, final BomToolFinderOptions options)
            throws BomToolException, DetectUserFriendlyException {

        final List<BomToolEvaluation> results = new ArrayList<>();

        if (depth > options.getMaximumDepth()) {
            return results;
        }

        if (null == directoriesToSearch || directoriesToSearch.size() == 0) {
            return results;
        }

        for (final File directory : directoriesToSearch) {
            if (options.getExcludedDirectories().contains(directory.getName())) {
                logger.info("Skipping excluded directory: " + directory.getPath());
                continue;
            }

            logger.info("Searching directory: " + directory.getPath());

            final Set<BomToolGroupType> applicableTypes = new HashSet<>();
            final Set<BomToolType> applied = new HashSet<>();
            final List<BomToolEvaluation> evaluations = processDirectory(bomToolFactory, directory, appliedBefore, depth, options);
            results.addAll(evaluations);
            applied.addAll(evaluations.stream().map(it -> it.getBomTool().getBomToolType()).collect(Collectors.toList()));

            //TODO: Used to have a remaining strategies and would bail early here, not sure how to go about that?
            final Set<BomToolType> everApplied = new HashSet<>();
            everApplied.addAll(applied);
            everApplied.addAll(appliedBefore);
            final List<File> subdirectories = getSubDirectories(directory, options.getExcludedDirectories());
            final List<BomToolEvaluation> recursiveResults = findApplicableBomTools(bomToolFactory, subdirectories, everApplied, depth + 1, options);
            results.addAll(recursiveResults);

            logger.debug(directory + ": " + applicableTypes.stream().map(it -> it.toString()).collect(Collectors.joining(", ")));
        }

        return results;
    }

    private List<BomToolEvaluation> processDirectory(final BomToolFactory bomToolFactory, final File directory, final Set<BomToolType> appliedBefore, final int depth, final BomToolFinderOptions options) {
        final BomToolEnvironment environment = new BomToolEnvironment(directory, appliedBefore, depth, options.getBomToolFilter(), options.getForceNestedSearch());
        final BomToolSearchRuleSet bomToolSet = bomToolFactory.createStrategies(environment);
        final List<BomToolEvaluation> evaluations = bomToolSet.evaluate();
        return evaluations;
    }

    private List<File> getSubDirectories(final File directory, final List<String> excludedDirectories) throws DetectUserFriendlyException {
        Stream<Path> stream = null;
        try {
            // only include directories that do not match the excluded directories
            final Predicate<File> excludeDirectoriesPredicate = file -> {
                boolean matchesExcludedDirectory = false;
                for (final String excludedDirectory : excludedDirectories) {
                    if (FilenameUtils.wildcardMatchOnSystem(file.getName(), excludedDirectory)) {
                        matchesExcludedDirectory = true;
                        break;
                    }
                }
                return !matchesExcludedDirectory;
            };

            stream = Files.list(directory.toPath());
            return stream.map(path -> path.toFile())
                    .filter(file -> file.isDirectory())
                    .filter(excludeDirectoriesPredicate)
                    .collect(Collectors.toList());

        } catch (final IOException e) {
            throw new DetectUserFriendlyException(String.format("Could not get the subdirectories for %s. %s", directory.getAbsolutePath(), e.getMessage()), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }
}
