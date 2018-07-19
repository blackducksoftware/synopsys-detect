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
package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.util.List;

import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;

public class ExtractionSummaryData {
    private final String directory;

    private final List<BomToolEvaluation> success;
    private final List<BomToolEvaluation> failed;
    private final List<BomToolEvaluation> exception;
    private final List<String> codeLocationNames;

    public ExtractionSummaryData(final String directory, final List<BomToolEvaluation> success, final List<BomToolEvaluation> failed, final List<BomToolEvaluation> exception, final List<String> codeLocationNames) {
        this.directory = directory;
        this.success = success;
        this.failed = failed;
        this.exception = exception;
        this.codeLocationNames = codeLocationNames;
    }

    public String getDirectory() {
        return directory;
    }

    public List<BomToolEvaluation> getSuccess() {
        return success;
    }

    public List<BomToolEvaluation> getFailed() {
        return failed;
    }

    public List<BomToolEvaluation> getException() {
        return exception;
    }

    public List<String> getCodeLocationNames() {
        return codeLocationNames;
    }
}
