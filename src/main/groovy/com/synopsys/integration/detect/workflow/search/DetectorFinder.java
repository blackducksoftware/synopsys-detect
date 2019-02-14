/**
 * synopsys-detect
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
package com.synopsys.integration.detect.workflow.search;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.detector.Detector;
import com.synopsys.integration.detect.detector.DetectorEnvironment;
import com.synopsys.integration.detect.detector.DetectorException;
import com.synopsys.integration.detect.detector.DetectorType;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.workflow.search.result.DetectorEvaluation;
import com.synopsys.integration.detect.workflow.search.rules.DetectorSearchRuleSet;

public class DetectorFinder {
    private final Logger logger = LoggerFactory.getLogger(DetectorFinder.class);

    public List<DetectorEvaluation> findApplicableBomTools(final File initialDirectory, final DetectorFinderOptions options) throws DetectorException, DetectUserFriendlyException {
        final List<File> subDirectories = new ArrayList<>();
        subDirectories.add(initialDirectory);
        return findApplicableBomTools(subDirectories, new HashSet<Detector>(), 0, options);
    }

    private List<DetectorEvaluation> findApplicableBomTools(final List<File> directoriesToSearch, final Set<Detector> appliedBefore, final int depth, final DetectorFinderOptions options)
        throws DetectorException, DetectUserFriendlyException {

        final List<DetectorEvaluation> results = new ArrayList<>();

        if (depth > options.getMaximumDepth()) {
            return results;
        }

        if (null == directoriesToSearch || directoriesToSearch.size() == 0) {
            return results;
        }

        for (final File directory : directoriesToSearch) {
            if (depth > 0 && options.getDetectorSearchFilter().shouldExclude(directory)) { // NEVER skip at depth 0.
                logger.info("Skipping excluded directory: " + directory.getPath());
                continue;
            }

            logger.info("Searching directory: " + directory.getPath());

            final Set<DetectorType> applicableTypes = new HashSet<>();
            final Set<Detector> applied = new HashSet<>();
            final List<DetectorEvaluation> evaluations = processDirectory(directory, appliedBefore, depth, options);
            results.addAll(evaluations);

            final List<Detector> appliedBomTools = evaluations.stream()
                                                       .filter(it -> it.isApplicable())
                                                       .map(it -> it.getDetector())
                                                       .collect(Collectors.toList());

            applied.addAll(appliedBomTools);

            // TODO: Used to have a remaining detectors and would bail early here, not sure how to go about that?
            final Set<Detector> everApplied = new HashSet<>();
            everApplied.addAll(applied);
            everApplied.addAll(appliedBefore);
            final List<File> subdirectories = getSubDirectories(directory, options.getDetectorSearchFilter());
            final List<DetectorEvaluation> recursiveResults = findApplicableBomTools(subdirectories, everApplied, depth + 1, options);
            results.addAll(recursiveResults);

            logger.debug(directory + ": " + applicableTypes.stream().map(it -> it.toString()).collect(Collectors.joining(", ")));
        }

        return results;
    }

    private List<DetectorEvaluation> processDirectory(final File directory, final Set<Detector> appliedBefore, final int depth, final DetectorFinderOptions options) {
        final DetectorEnvironment environment = new DetectorEnvironment(directory, appliedBefore, depth, options.getDetectorFilter(), options.getForceNestedSearch());
        final DetectorSearchRuleSet bomToolSet = options.getDetectorSearchProvider().createBomToolSearchRuleSet(environment);
        final List<DetectorEvaluation> evaluations = options.getDetectorSearchEvaluator().evaluate(bomToolSet, options.getEventSystem());
        return evaluations;
    }

    private List<File> getSubDirectories(final File directory, DetectorSearchFilter filter) throws DetectUserFriendlyException {
        Stream<Path> stream = null;
        try {
            stream = Files.list(directory.toPath());
            return stream.map(path -> path.toFile())
                       .filter(file -> file.isDirectory())
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
