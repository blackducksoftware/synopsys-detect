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
package com.blackducksoftware.integration.hub.detect.bomtool.search;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;
import com.blackducksoftware.integration.hub.detect.extraction.StrategyEvaluation;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyEnvironment;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class BomToolFinder {
    private final Logger logger = LoggerFactory.getLogger(BomToolFinder.class);

    public List<StrategyEvaluation> findApplicableBomTools(final Set<Strategy> strategies, final File initialDirectory, final BomToolFinderOptions options) throws BomToolException, DetectUserFriendlyException {

        final List<File> subDirectories = new ArrayList<>();
        subDirectories.add(initialDirectory);
        final List<Strategy> orderedStrategies = determineOrder(strategies);
        return findApplicableBomTools(orderedStrategies, subDirectories, new HashSet<Strategy>(), 0, options);
    }

    private List<StrategyEvaluation> findApplicableBomTools(final List<Strategy> orderedStrategies, final List<File> directoriesToSearch, final Set<Strategy> appliedBefore, final int depth, final BomToolFinderOptions options)
            throws BomToolException, DetectUserFriendlyException {

        final List<StrategyEvaluation> results = new ArrayList<>();

        if (depth > options.getMaximumDepth()) {
            return results;
        }

        if (null == directoriesToSearch || directoriesToSearch.size() == 0) {
            return results;
        }

        for (final File directory : directoriesToSearch) {
            if (options.getExcludedDirectories().contains(directory.getName())){
                continue;
            }

            logger.info("Searching directory: " + directory.getPath());

            final Set<BomToolType> applicableTypes = new HashSet<>();
            final List<Strategy> remainingStrategies = new ArrayList<>();
            final Set<Strategy> alreadyApplied = new HashSet<>();
            for (final Strategy strategy : orderedStrategies) {
                final StrategyEvaluation result = processStrategy(strategy, directory, alreadyApplied, appliedBefore, depth, options);
                if (result.isApplicable()) {
                    alreadyApplied.add(strategy);
                }
                remainingStrategies.add(strategy);
                results.add(result);
            }
            if (remainingStrategies.size() > 0) {
                final Set<Strategy> everApplied = new HashSet<>();
                everApplied.addAll(alreadyApplied);
                everApplied.addAll(appliedBefore);
                final List<File> subdirectories = getSubDirectories(directory, options.getExcludedDirectories());
                final List<StrategyEvaluation> recursiveResults = findApplicableBomTools(remainingStrategies, subdirectories, everApplied, depth + 1, options);
                results.addAll(recursiveResults);
            }
            logger.debug(directory + ": " + applicableTypes.stream().map(it -> it.toString()).collect(Collectors.joining(", ")));
        }

        return results;
    }

    private StrategyEvaluation processStrategy(final Strategy strategy, final File directory, final Set<Strategy> appliedCurrent, final Set<Strategy> appliedBefore, final int depth, final BomToolFinderOptions options) {
        final StrategyEnvironment environment = new StrategyEnvironment(directory, appliedCurrent, appliedBefore, depth, options.getBomToolFilter(), options.getForceNestedSearch());
        final ExtractionContext context = strategy.createContext(directory);
        final StrategyEvaluation evaluation = new StrategyEvaluation(strategy, environment, context);

        evaluation.searchable = strategy.searchable(environment, context);
        if (evaluation.searchable.getPassed()) {
            evaluation.applicable = strategy.applicable(environment, context);
        }

        return evaluation;
    }

    private List<Strategy> determineOrder(final Set<Strategy> allStrategies) throws DetectUserFriendlyException{
        final Set<Strategy> remaining = new HashSet<>(allStrategies);
        final List<Strategy> ordered = new ArrayList<>();

        boolean stalled = false;

        while (remaining.size() > 0 && !stalled) {
            Strategy next = null;
            for (final Strategy remainingStrategy : remaining) {
                final boolean yieldSatisfied = containsAllYield(remainingStrategy, ordered);
                if (yieldSatisfied) {
                    next = remainingStrategy;
                    break;
                }
            }

            if (next != null) {
                stalled = false;
                remaining.remove(next);
                ordered.add(next);
            } else {
                stalled = true;
            }
        }

        if (ordered.size() != allStrategies.size()) {
            throw new DetectUserFriendlyException("An error occured finding extraction strategy order.", ExitCodeType.FAILURE_CONFIGURATION);
        }
        return ordered;
    }

    private boolean containsAllYield(final Strategy target, final List<Strategy> existingStrategies) {
        boolean containsAll = true;
        final Set<Strategy> yieldsTo = target.getYieldsToStrategies();
        for (final Strategy yieldToStrategy : yieldsTo) {
            containsAll = containsAll && existingStrategies.contains(yieldToStrategy);
        }
        return containsAll;
    }

    private List<File> getSubDirectories(final File directory, final List<String> excludedDirectories) throws DetectUserFriendlyException {
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

            return Files.list(directory.toPath())
                    .map(path -> path.toFile())
                    .filter(file -> file.isDirectory())
                    .filter(excludeDirectoriesPredicate)
                    .collect(Collectors.toList());
        } catch (final IOException e) {
            throw new DetectUserFriendlyException(String.format("Could not get the subdirectories for %s. %s", directory.getAbsolutePath(), e.getMessage()), e, ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }
}
