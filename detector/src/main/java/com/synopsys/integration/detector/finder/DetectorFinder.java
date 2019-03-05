/**
 * detector
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
package com.synopsys.integration.detector.finder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detector.base.DetectorEvaluationTree;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.rule.DetectorRuleSet;

public class DetectorFinder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Optional<DetectorEvaluationTree> findDetectors(final File initialDirectory, DetectorRuleSet detectorRuleSet, final DetectorFinderOptions options) throws DetectorFinderDirectoryListException {
        return findDetectors(initialDirectory, detectorRuleSet,0, options);
    }

    private Optional<DetectorEvaluationTree> findDetectors(final File directory, DetectorRuleSet detectorRuleSet, final int depth, final DetectorFinderOptions options)
        throws DetectorFinderDirectoryListException {

        if (depth > options.getMaximumDepth()) {
            return Optional.empty();
        }

        if (null == directory || !directory.isDirectory()) {
            return Optional.empty();
        }

        if (depth > 0 && options.getFileFilter().test(directory)) { // NEVER skip at depth 0.
            logger.info("Skipping excluded directory: " + directory.getPath());
            return Optional.empty();
        }

        logger.info("Searching directory: " + directory.getPath());
        List<DetectorEvaluation> evaluations = detectorRuleSet.getOrderedDetectorRules().stream()
                                                   .map(rule -> new DetectorEvaluation(rule))
                                                   .collect(Collectors.toList());

        Set<DetectorEvaluationTree> children = new HashSet<>();

        List<File> subDirectories = findSubDirectories(directory);
        for (final File subDirectory : subDirectories) {
            Optional<DetectorEvaluationTree> childEvaluationSet = findDetectors(subDirectory, detectorRuleSet, depth + 1, options);
            if (childEvaluationSet.isPresent()){
                children.add(childEvaluationSet.get());
            }
        }

        return Optional.of(new DetectorEvaluationTree(directory, depth, detectorRuleSet, evaluations, children));
    }

    private List<File> findSubDirectories(final File directory) throws DetectorFinderDirectoryListException {
        Stream<Path> stream = null;
        try {
            stream = Files.list(directory.toPath());
            return stream.map(path -> path.toFile())
                       .filter(file -> file.isDirectory())
                       .collect(Collectors.toList());

        } catch (final IOException e) {
            throw new DetectorFinderDirectoryListException(String.format("Could not get the subdirectories for %s. %s", directory.getAbsolutePath(), e.getMessage()), e);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }
}
