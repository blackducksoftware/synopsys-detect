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
package com.blackducksoftware.integration.hub.detect.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.bomtool.search.report.SearchSummaryReporter;
import com.blackducksoftware.integration.hub.detect.configuration.BomToolConfig;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfig;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.extraction.model.StrategyEvaluation;
import com.blackducksoftware.integration.hub.detect.manager.result.search.SearchResult;
import com.blackducksoftware.integration.hub.detect.manager.result.search.SearchResultBomToolFailed;
import com.blackducksoftware.integration.hub.detect.manager.result.search.SearchResultSuccess;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.search.BomToolFinder;
import com.blackducksoftware.integration.hub.detect.search.BomToolFinderOptions;
import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.strategy.StrategyManager;
import com.blackducksoftware.integration.util.ExcludedIncludedFilter;

@Component
public class SearchManager {
    private final Logger logger = LoggerFactory.getLogger(SearchManager.class);

    private final SearchSummaryReporter searchSummaryReporter;
    private final StrategyManager strategyManager;
    private final DetectConfig detectConfig;
    private final BomToolConfig bomToolConfig;
    private final DetectPhoneHomeManager detectPhoneHomeManager;

    @Autowired
    public SearchManager(final SearchSummaryReporter searchSummaryReporter, final StrategyManager strategyManager, final DetectConfig detectConfig, final BomToolConfig bomToolConfig, final DetectPhoneHomeManager detectPhoneHomeManager) {
        this.searchSummaryReporter = searchSummaryReporter;
        this.strategyManager = strategyManager;
        this.detectConfig = detectConfig;
        this.bomToolConfig = bomToolConfig;
        this.detectPhoneHomeManager = detectPhoneHomeManager;
    }

    private List<StrategyEvaluation> findApplicableBomTools(final File directory) throws BomToolException, DetectUserFriendlyException {
        final List<Strategy> allStrategies = strategyManager.getAllStrategies();
        final List<String> excludedDirectories = bomToolConfig.getBomToolSearchDirectoryExclusions();
        final Boolean forceNestedSearch = bomToolConfig.getBomToolContinueSearch();
        final int maxDepth = bomToolConfig.getBomToolSearchDepth();
        final ExcludedIncludedFilter bomToolFilter = new ExcludedIncludedFilter(bomToolConfig.getExcludedBomToolTypes(), bomToolConfig.getIncludedBomToolTypes());
        final BomToolFinderOptions findOptions = new BomToolFinderOptions(excludedDirectories, forceNestedSearch, maxDepth, bomToolFilter);

        logger.info("Starting search for bom tools.");
        final BomToolFinder bomToolTreeWalker = new BomToolFinder();
        return bomToolTreeWalker.findApplicableBomTools(new HashSet<>(allStrategies), directory, findOptions);
    }

    public SearchResult performSearch() throws DetectUserFriendlyException {
        List<StrategyEvaluation> sourcePathResults = new ArrayList<>();
        try {
            sourcePathResults = findApplicableBomTools(new File(detectConfig.getSourcePath()));
        } catch (final BomToolException e) {
            return new SearchResultBomToolFailed(e);
        }

        searchSummaryReporter.print(sourcePathResults);

        final Set<BomToolType> applicableBomTools = sourcePathResults.stream()
                .filter(it -> it.isApplicable())
                .map(it -> it.strategy.getBomToolType())
                .collect(Collectors.toSet());

        // we've gone through all applicable bom tools so we now have the complete metadata to phone home
        detectPhoneHomeManager.startPhoneHome(applicableBomTools);

        return new SearchResultSuccess(sourcePathResults);
    }
}
