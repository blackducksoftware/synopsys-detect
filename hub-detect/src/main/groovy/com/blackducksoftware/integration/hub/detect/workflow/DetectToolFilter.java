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
package com.blackducksoftware.integration.hub.detect.workflow;

import java.util.Optional;

import com.blackducksoftware.integration.hub.detect.DetectTool;
import com.synopsys.integration.util.ExcludedIncludedFilter;

public class DetectToolFilter {

    private final ExcludedIncludedFilter excludedIncludedFilter;
    private final Optional<Boolean> deprecatedSigScanDisabled;
    private final Optional<Boolean> deprecatedSwipEnabled;

    public DetectToolFilter(String includedTools, String excludedTools, Optional<Boolean> deprecatedSigScanDisabled, Optional<Boolean> deprecatedSwipEnabled) {
        this.excludedIncludedFilter = new ExcludedIncludedFilter(includedTools, excludedTools);

        this.deprecatedSigScanDisabled = deprecatedSigScanDisabled;
        this.deprecatedSwipEnabled = deprecatedSwipEnabled;
    }

    public boolean shouldInclude(DetectTool detectTool) {
        if (detectTool == DetectTool.SIGNATURE_SCAN && deprecatedSigScanDisabled.isPresent()){
            return !deprecatedSigScanDisabled.get();
        }else if (detectTool == DetectTool.SWIP_CLI && deprecatedSwipEnabled.isPresent()){
            return  deprecatedSwipEnabled.get();
        }

        return excludedIncludedFilter.shouldInclude(detectTool.name());
    }
}
