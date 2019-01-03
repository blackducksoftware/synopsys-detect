/**
 * hub-detect
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
package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.util.Map;

public class OverviewSummaryData {
    private final String directory;
    private final String detectorName;
    private final boolean wasExtractable;
    private final boolean wasExtracted;

    private Map<String, String> associatedData;
    private String errorReason;

    public OverviewSummaryData(final String directory, final String detectorName, final boolean wasExtractable, final boolean wasExtracted, final Map<String, String> associatedData, final String errorReason) {
        this.directory = directory;
        this.detectorName = detectorName;
        this.wasExtractable = wasExtractable;
        this.wasExtracted = wasExtracted;
        this.associatedData = associatedData;
        this.errorReason = errorReason;
    }

    public String getDirectory() {
        return directory;
    }

    public String getDetectorName() {
        return detectorName;
    }

    public boolean wasExtractable() {
        return wasExtractable;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public Map<String, String> getAssociatedData() {
        return associatedData;
    }

    public boolean wasExtracted() {
        return wasExtracted;
    }
}
