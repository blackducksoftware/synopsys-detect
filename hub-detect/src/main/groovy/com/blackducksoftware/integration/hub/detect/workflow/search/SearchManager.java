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
package com.blackducksoftware.integration.hub.detect.workflow.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.search.rules.BomToolSearchEvaluator;
import com.blackducksoftware.integration.hub.detect.workflow.search.rules.BomToolSearchProvider;

public class SearchManager {
    private final Logger logger = LoggerFactory.getLogger(SearchManager.class);

    private final SearchOptions searchOptions;
    private final BomToolSearchProvider bomToolSearchProvider;
    private final BomToolSearchEvaluator bomToolSearchEvaluator;
    private final EventSystem eventSystem;

    public SearchManager(final SearchOptions searchOptions, final BomToolSearchProvider bomToolSearchProvider, final BomToolSearchEvaluator bomToolSearchEvaluator, EventSystem eventSystem) { //TODO: replace bom tool profiling
        this.searchOptions = searchOptions;
        this.bomToolSearchProvider = bomToolSearchProvider;
        this.bomToolSearchEvaluator = bomToolSearchEvaluator;
        this.eventSystem = eventSystem;
    }

    public SearchResult performSearch() {
        List<BomToolEvaluation> searchResults = new ArrayList<>();
        try {
            final BomToolFinderOptions findOptions = new BomToolFinderOptions(searchOptions.excludedDirectories, searchOptions.forceNestedSearch, searchOptions.maxDepth, searchOptions.bomToolFilter, bomToolSearchProvider,
                bomToolSearchEvaluator, eventSystem);

            logger.info("Starting search for bom tools.");
            final BomToolFinder bomToolTreeWalker = new BomToolFinder();
            searchResults = bomToolTreeWalker.findApplicableBomTools(searchOptions.searchPath, findOptions);
        } catch (final BomToolException e) {
            return new SearchResultBomToolFailed(e);
        } catch (DetectUserFriendlyException e) {
            e.printStackTrace();
        }

        final Set<BomToolGroupType> applicableBomTools = searchResults.stream()
                                                             .filter(it -> it.isApplicable())
                                                             .map(it -> it.getBomTool().getBomToolGroupType())
                                                             .collect(Collectors.toSet());

        return new SearchResultSuccess(searchResults, applicableBomTools);
    }

}
