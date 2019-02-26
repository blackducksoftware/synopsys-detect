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

import com.synopsys.integration.detector.DetectorEventListener;
import com.synopsys.integration.detector.rules.DetectorSearchEvaluator;
import com.synopsys.integration.detector.rules.DetectorSearchProvider;
import com.synopsys.integration.detector.search.DetectorSearchFilter;

public class DetectorFinderOptions {

    private final DetectorSearchFilter detectorSearchFilter;
    private final Boolean forceNestedSearch;
    private final int maximumDepth;
    private final DetectorFilter detectorFilter;
    private final DetectorSearchProvider detectorSearchProvider;
    private final DetectorSearchEvaluator detectorSearchEvaluator;
    private final DetectorEventListener detectorEventListener;

    public DetectorFinderOptions(DetectorSearchFilter detectorSearchFilter, final Boolean forceNestedSearch, final int maximumDepth, final DetectorFilter detectorFilter,
        final DetectorSearchProvider detectorSearchProvider, final DetectorSearchEvaluator detectorSearchEvaluator, DetectorEventListener detectorEventListener) {
        this.detectorSearchFilter = detectorSearchFilter;
        this.forceNestedSearch = forceNestedSearch;
        this.maximumDepth = maximumDepth;
        this.detectorFilter = detectorFilter;
        this.detectorSearchProvider = detectorSearchProvider;
        this.detectorSearchEvaluator = detectorSearchEvaluator;
        this.detectorEventListener = detectorEventListener;
    }

    public DetectorSearchFilter getDetectorSearchFilter() {
        return detectorSearchFilter;
    }

    public Boolean getForceNestedSearch() {
        return forceNestedSearch;
    }

    public DetectorFilter getDetectorFilter() {
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

    public DetectorEventListener getDetectorEventListener() {
        return detectorEventListener;
    }
}
