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
package com.synopsys.integration.detector.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detector.DetectorEventListener;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detector.exception.DetectorException;
import com.synopsys.integration.detector.finder.DetectorFinder;
import com.synopsys.integration.detector.finder.DetectorFinderDirectoryListException;
import com.synopsys.integration.detector.finder.DetectorFinderOptions;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.rules.DetectorSearchEvaluator;
import com.synopsys.integration.detector.rules.DetectorSearchProvider;
import com.synopsys.integration.detector.search.DetectorExclusionSearchFilter;
import com.synopsys.integration.detector.search.SearchOptions;
import com.synopsys.integration.detector.search.SearchResultSuccess;

public class ApplicableEvaluator {
    private final Logger logger = LoggerFactory.getLogger(ApplicableEvaluator.class);

    private final SearchOptions searchOptions;
    private final DetectorSearchProvider detectorSearchProvider;
    private final DetectorSearchEvaluator detectorSearchEvaluator;
    private final DetectorEventListener detectorEventListener;

    public ApplicableEvaluator(final SearchOptions searchOptions, final DetectorSearchProvider detectorSearchProvider, final DetectorSearchEvaluator detectorSearchEvaluator, final DetectorEventListener detectorEventListener) {
        this.searchOptions = searchOptions;
        this.detectorSearchProvider = detectorSearchProvider;
        this.detectorSearchEvaluator = detectorSearchEvaluator;
        this.detectorEventListener = detectorEventListener;
    }

    public SearchResult performSearch() throws DetectorFinderDirectoryListException {
        List<DetectorEvaluation> searchResults = new ArrayList<>();
        try {
            DetectorExclusionSearchFilter detectorExclusionSearchFilter = new DetectorExclusionSearchFilter(searchOptions.excludedDirectories, searchOptions.excludedDirectoryPatterns);
            final DetectorFinderOptions findOptions = new DetectorFinderOptions(detectorExclusionSearchFilter, searchOptions.forceNestedSearch, searchOptions.maxDepth, searchOptions.detectorFilter, detectorSearchProvider, detectorSearchEvaluator, detectorEventListener);

            logger.info("Starting search for detectors.");
            final DetectorFinder bomToolTreeWalker = new DetectorFinder();
            searchResults = bomToolTreeWalker.findApplicableBomTools(searchOptions.searchPath, findOptions);
        } catch (final DetectorException e) {
            //return new SearchResultBomToolFailed(e);
        }

        final Set<DetectorType> applicableBomTools = searchResults.stream()
                                                         .filter(it -> it.isApplicable())
                                                         .map(it -> it.getDetector().getDetectorType())
                                                         .collect(Collectors.toSet());

        return new SearchResultSuccess(searchResults, applicableBomTools);
    }
}
