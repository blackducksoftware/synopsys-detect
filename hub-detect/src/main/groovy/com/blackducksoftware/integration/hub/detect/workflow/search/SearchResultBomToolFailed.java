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
package com.blackducksoftware.integration.hub.detect.workflow.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.blackducksoftware.integration.hub.detect.detector.DetectorException;
import com.blackducksoftware.integration.hub.detect.detector.DetectorType;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorEvaluation;

public class SearchResultBomToolFailed extends SearchResult {
    private final DetectorException exception;

    public SearchResultBomToolFailed(final DetectorException exception) {
        this.exception = exception;
    }

    public DetectorException getException() {
        return exception;
    }

    @Override
    public List<DetectorEvaluation> getDetectorEvaluations() {
        return new ArrayList<>();
    }

    @Override
    public Set<DetectorType> getApplicableBomTools() {
        return new HashSet<>();
    }

    @Override
    public boolean getSuccess() {
        return false;
    }
}
