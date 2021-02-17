/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detect.util.filter;

import java.util.Optional;

import com.synopsys.integration.detect.configuration.ExcludeIncludeEnumFilter;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;

public class DetectToolFilter {
    private final ExcludeIncludeEnumFilter<DetectTool> excludedIncludedFilter;
    private final Optional<Boolean> deprecatedSigScanDisabled;
    private final Optional<Boolean> deprecatedPolarisEnabled;

    public DetectToolFilter(ExcludeIncludeEnumFilter<DetectTool> excludedIncludedFilter, final Optional<Boolean> deprecatedSigScanDisabled, final Optional<Boolean> deprecatedPolarisEnabled) {
        this.excludedIncludedFilter = excludedIncludedFilter;

        this.deprecatedSigScanDisabled = deprecatedSigScanDisabled;
        this.deprecatedPolarisEnabled = deprecatedPolarisEnabled;
    }

    public boolean shouldInclude(final DetectTool detectTool) {

        if (detectTool == DetectTool.SIGNATURE_SCAN && deprecatedSigScanDisabled.isPresent()) {
            return !deprecatedSigScanDisabled.get();
        } else if (detectTool == DetectTool.POLARIS && deprecatedPolarisEnabled.isPresent()) {
            return deprecatedPolarisEnabled.get();
        }

        return excludedIncludedFilter.shouldInclude(detectTool);
    }
}
