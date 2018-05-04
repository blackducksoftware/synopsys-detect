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

import com.blackducksoftware.integration.hub.detect.bomtool.search.StrategyFindResult.FindType;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.evaluation.StrategyEvaluation;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.evaluation.StrategyEvaluator;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class BomToolFinder {
    private final Logger logger = LoggerFactory.getLogger(BomToolFinder.class);

    // Think three stages:
    // 1. Search for applicable (nuget applies).
    //      Complications:
    //          Docker: not the most straightforward applies - uses properties not necessarily files to indicate
    //          GO uses other GO applicables to decide
    //          NPM uses YARN applicables to decide
    // 2. Check the environment (nuget exists, install inspector) just once (instead of every applicable).
    //      Complications:
    //          Some executable inside the source directory, gradle?
    //          Right now some inspectors resolve during configuration init if the BomTool applies.
    //          We will no longer know during init if the bom tool applies.
    // 3. Execute applicable (nuget extracts)
    //      This will have multiple stages. Might be complex due to cleanup.
    //      I'd also like to let bom tools nominate project names
    // 4. Transform results.

    public List<StrategyFindResult> findApplicableBomTools(final Set<Strategy> strategies, final StrategyEvaluator strategyEvaluator, final File initialDirectory, final BomToolFinderOptions options) throws BomToolException, DetectUserFriendlyException {
        final List<File> subDirectories = new ArrayList<>();
        subDirectories.add(initialDirectory);
        final List<Strategy> orderedStrategies = determineOrder(strategies);
        return findApplicableBomTools(orderedStrategies, strategyEvaluator, subDirectories, 1, options);
    }

    private List<StrategyFindResult> findApplicableBomTools(final List<Strategy> orderedStrategies, final StrategyEvaluator strategyEvaluator, final List<File> directoriesToSearch, final int depth, final BomToolFinderOptions options)
            throws BomToolException, DetectUserFriendlyException {

        final List<StrategyFindResult> results = new ArrayList<>();

        if (depth > options.getMaximumDepth()) {
            return results;
        }

        if (null == directoriesToSearch || directoriesToSearch.size() == 0) {
            return results;
        }

        for (final File directory : directoriesToSearch) {
            final EvaluationContext evaluationContext = new EvaluationContext(directory);

            final Set<BomToolType> applicableTypes = new HashSet<>();
            final List<Strategy> remainingStrategies = new ArrayList<>();
            final Set<Strategy> alreadyApplied = new HashSet<>();
            for (final Strategy strategy : orderedStrategies) {
                final StrategyFindResult result = processStrategy(strategy, strategyEvaluator, evaluationContext, alreadyApplied);
                if (result.type == FindType.APPLIES) {
                    alreadyApplied.add(strategy);
                    remainingStrategies.add(strategy);
                } else {
                    remainingStrategies.add(strategy);
                }
                results.add(result);
            }
            if (remainingStrategies.size() > 0) {
                final List<File> subdirectories = getSubDirectories(directory, options.getExcludedDirectories());
                final List<StrategyFindResult> recursiveResults = findApplicableBomTools(remainingStrategies, strategyEvaluator, subdirectories, depth + 1, options);
                results.addAll(recursiveResults);
            }
            logger.debug(directory + ": " + applicableTypes.stream().map(it -> it.toString()).collect(Collectors.joining(", ")));
        }

        return results;
    }

    private StrategyFindResult processStrategy(final Strategy strategy, final StrategyEvaluator strategyEvaluator, final EvaluationContext context, final Set<Strategy> alreadyApplied) {
        final StrategyEvaluation evaluation = new StrategyEvaluation();
        if (containsAnyYield(strategy, alreadyApplied, evaluation)) {
            return new StrategyFindResult(strategy, FindType.YIELDED, evaluation, context);
        } else {
            strategyEvaluator.fulfillsRequirements(evaluation, strategy, context);
            if (evaluation.areNeedsMet()) {
                return new StrategyFindResult(strategy, FindType.APPLIES, evaluation, context);
            }else {
                return new StrategyFindResult(strategy, FindType.NEEDS_NOT_MET, evaluation, context);
            }
        }
    }

    @SuppressWarnings({ "rawtypes" })
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

    private boolean containsAnyYield(final Strategy target, final Set<Strategy> existingStrategies, final StrategyEvaluation evaluation) {
        final Set<Strategy> yieldsTo = target.getYieldsToStrategies();
        boolean yieldedTo = false;
        for (final Strategy yieldToStrategy : yieldsTo) {
            if (existingStrategies.contains(yieldToStrategy)) {
                evaluation.addYieldedStrategy(yieldToStrategy);
                yieldedTo = true;
            }
        }
        return yieldedTo;
    }

    private boolean shouldStopSearchingIfApplicable(final Strategy strategy, final BomToolFinderOptions options) {
        if (options.getForceNestedSearch()) {
            return false;
        }
        //TODO: Replicate
        //if (strategy.getSearchOptions().canSearchWithinApplicableDirectories()) {
        //    return false;
        //}
        return true;
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
