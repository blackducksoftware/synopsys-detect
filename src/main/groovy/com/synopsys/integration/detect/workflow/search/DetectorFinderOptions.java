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

import com.synopsys.integration.detect.util.filter.DetectFilter;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.search.rules.DetectorSearchEvaluator;
import com.synopsys.integration.detect.workflow.search.rules.DetectorSearchProvider;

public class DetectorFinderOptions {

    private final DetectorSearchFilter detectorSearchFilter;
    private final Boolean forceNestedSearch;
    private final int maximumDepth;
    private final DetectFilter detectorFilter;
    private final DetectorSearchProvider detectorSearchProvider;
    private final DetectorSearchEvaluator detectorSearchEvaluator;
    private final EventSystem eventSystem;

    public DetectorFinderOptions(DetectorSearchFilter detectorSearchFilter, final Boolean forceNestedSearch, final int maximumDepth, final DetectFilter detectorFilter,
        final DetectorSearchProvider detectorSearchProvider, final DetectorSearchEvaluator detectorSearchEvaluator, EventSystem eventSystem) {
        this.detectorSearchFilter = detectorSearchFilter;
        this.forceNestedSearch = forceNestedSearch;
        this.maximumDepth = maximumDepth;
        this.detectorFilter = detectorFilter;
        this.detectorSearchProvider = detectorSearchProvider;
        this.detectorSearchEvaluator = detectorSearchEvaluator;
        this.eventSystem = eventSystem;
    }

    public DetectorSearchFilter getDetectorSearchFilter() {
        return detectorSearchFilter;
    }

    public Boolean getForceNestedSearch() {
        return forceNestedSearch;
    }

    public DetectFilter getDetectorFilter() {
        return detectorFilter;
    }

    public int getMaximumDepth() {
        return maximumDepth;
    }

    public DetectorSearchProvider getDetectorSearchProvider() {
        return detectorSearchProvider;
    }

    public DetectorSearchEvaluator getDetectorSearchEvaluator() {
        return detectorSearchEvaluator;
    }

    public EventSystem getEventSystem() {
        return eventSystem;
    }
}
