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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigWrapper;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.workflow.PhoneHomeManager;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.search.rules.BomToolSearchProvider;
import com.blackducksoftware.integration.util.ExcludedIncludedFilter;

public class SearchManager {
    private final Logger logger = LoggerFactory.getLogger(SearchManager.class);

    private final SearchSummaryReporter searchSummaryReporter;
    private final BomToolSearchProvider bomToolSearchProvider;
    private final PhoneHomeManager phoneHomeManager;
    private final DetectConfigWrapper detectConfigWrapper;

    public SearchManager(final SearchSummaryReporter searchSummaryReporter, final BomToolSearchProvider bomToolSearchProvider, final PhoneHomeManager phoneHomeManager,
            final DetectConfigWrapper detectConfigWrapper) {
        this.searchSummaryReporter = searchSummaryReporter;
        this.bomToolSearchProvider = bomToolSearchProvider;
        this.phoneHomeManager = phoneHomeManager;
        this.detectConfigWrapper = detectConfigWrapper;
    }

    public SearchResult performSearch() throws DetectUserFriendlyException {
        List<BomToolEvaluation> sourcePathResults = new ArrayList<>();
        try {
            sourcePathResults = findApplicableBomTools(new File(detectConfigWrapper.getProperty(DetectProperty.DETECT_SOURCE_PATH)));
        } catch (final BomToolException e) {
            return new SearchResultBomToolFailed(e);
        }

        searchSummaryReporter.print(sourcePathResults);

        final Set<BomToolGroupType> applicableBomTools = sourcePathResults.stream()
                .filter(it -> it.isApplicable())
                .map(it -> it.getBomTool().getBomToolGroupType())
                .collect(Collectors.toSet());

        // we've gone through all applicable bom tools so we now have the complete metadata to phone home
        phoneHomeManager.startPhoneHome(applicableBomTools);

        return new SearchResultSuccess(sourcePathResults);
    }

    private List<BomToolEvaluation> findApplicableBomTools(final File directory) throws BomToolException, DetectUserFriendlyException {
        final List<String> excludedDirectories = Arrays.asList(detectConfigWrapper.getStringArrayProperty(DetectProperty.DETECT_BOM_TOOL_SEARCH_EXCLUSION));
        final Boolean forceNestedSearch = detectConfigWrapper.getBooleanProperty(DetectProperty.DETECT_BOM_TOOL_SEARCH_CONTINUE);
        final int maxDepth = detectConfigWrapper.getIntegerProperty(DetectProperty.DETECT_BOM_TOOL_SEARCH_DEPTH);
        final ExcludedIncludedFilter bomToolFilter = new ExcludedIncludedFilter(detectConfigWrapper.getProperty(DetectProperty.DETECT_EXCLUDED_BOM_TOOL_TYPES).toUpperCase(),
                detectConfigWrapper.getProperty(DetectProperty.DETECT_INCLUDED_BOM_TOOL_TYPES).toUpperCase());

        final BomToolFinderOptions findOptions = new BomToolFinderOptions(excludedDirectories, forceNestedSearch, maxDepth, bomToolFilter);

        logger.info("Starting search for bom tools.");
        final BomToolFinder bomToolTreeWalker = new BomToolFinder();
        return bomToolTreeWalker.findApplicableBomTools(bomToolSearchProvider, directory, findOptions);
    }
}
