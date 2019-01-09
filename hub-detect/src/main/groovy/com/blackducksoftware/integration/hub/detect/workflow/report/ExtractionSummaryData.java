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

import java.util.List;

import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorEvaluation;

public class ExtractionSummaryData {
    private final String directory;

    private final List<DetectorEvaluation> success;
    private final List<DetectorEvaluation> failed;
    private final List<DetectorEvaluation> exception;
    private final List<String> codeLocationNames;

    public ExtractionSummaryData(final String directory, final List<DetectorEvaluation> success, final List<DetectorEvaluation> failed, final List<DetectorEvaluation> exception, final List<String> codeLocationNames) {
        this.directory = directory;
        this.success = success;
        this.failed = failed;
        this.exception = exception;
        this.codeLocationNames = codeLocationNames;
    }

    public String getDirectory() {
        return directory;
    }

    public List<DetectorEvaluation> getSuccess() {
        return success;
    }

    public List<DetectorEvaluation> getFailed() {
        return failed;
    }

    public List<DetectorEvaluation> getException() {
        return exception;
    }

    public List<String> getCodeLocationNames() {
        return codeLocationNames;
    }
}
