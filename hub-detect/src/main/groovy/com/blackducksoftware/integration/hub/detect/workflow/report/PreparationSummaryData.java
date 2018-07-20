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

public class PreparationSummaryData {
    private final String directory;

    private final List<BomToolEvaluation> ready;
    private final List<BomToolEvaluation> failed;

    public PreparationSummaryData(final String directory, final List<BomToolEvaluation> ready, final List<BomToolEvaluation> failed) {
        this.directory = directory;
        this.ready = ready;
        this.failed = failed;
    }

    public String getDirectory() {
        return directory;
    }

    public List<BomToolEvaluation> getReady() {
        return ready;
    }

    public List<BomToolEvaluation> getFailed() {
        return failed;
    }
}
