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

import java.util.List;

import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.search.rules.BomToolSearchEvaluator;
import com.blackducksoftware.integration.hub.detect.workflow.search.rules.BomToolSearchProvider;
import com.synopsys.integration.util.ExcludedIncludedFilter;

public class BomToolFinderOptions {

    private final List<String> excludedDirectories;
    private final Boolean forceNestedSearch;
    private final int maximumDepth;
    private final ExcludedIncludedFilter bomToolFilter;
    private final BomToolSearchProvider bomToolSearchProvider;
    private final BomToolSearchEvaluator bomToolSearchEvaluator;
    private final EventSystem eventSystem;

    public BomToolFinderOptions(final List<String> excludedDirectories, final Boolean forceNestedSearch, final int maximumDepth, final ExcludedIncludedFilter bomToolFilter,
        final BomToolSearchProvider bomToolSearchProvider, final BomToolSearchEvaluator bomToolSearchEvaluator, EventSystem eventSystem) {
        this.excludedDirectories = excludedDirectories;
        this.forceNestedSearch = forceNestedSearch;
        this.maximumDepth = maximumDepth;
        this.bomToolFilter = bomToolFilter;
        this.bomToolSearchProvider = bomToolSearchProvider;
        this.bomToolSearchEvaluator = bomToolSearchEvaluator;
        this.eventSystem = eventSystem;
    }

    public List<String> getExcludedDirectories() {
        return excludedDirectories;
    }

    public Boolean getForceNestedSearch() {
        return forceNestedSearch;
    }

    public ExcludedIncludedFilter getBomToolFilter() {
        return bomToolFilter;
    }

    public int getMaximumDepth() {
        return maximumDepth;
    }

    public BomToolSearchProvider getBomToolSearchProvider() {
        return bomToolSearchProvider;
    }

    public BomToolSearchEvaluator getBomToolSearchEvaluator() {
        return bomToolSearchEvaluator;
    }

    public EventSystem getEventSystem() {
        return eventSystem;
    }
}
