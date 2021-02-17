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
package com.synopsys.integration.detect.tool.impactanalysis.service;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationRequest;
import com.synopsys.integration.blackduck.exception.BlackDuckIntegrationException;

public class ImpactAnalysisCodeLocationCreationRequest extends CodeLocationCreationRequest<ImpactAnalysisBatchOutput> {
    private final ImpactAnalysisBatchRunner impactAnalysisBatchRunner;
    private final ImpactAnalysisBatch impactAnalysisBatch;

    public ImpactAnalysisCodeLocationCreationRequest(ImpactAnalysisBatchRunner impactAnalysisBatchRunner, ImpactAnalysisBatch impactAnalysisBatch) {
        this.impactAnalysisBatchRunner = impactAnalysisBatchRunner;
        this.impactAnalysisBatch = impactAnalysisBatch;
    }

    @Override
    public ImpactAnalysisBatchOutput executeRequest() throws BlackDuckIntegrationException {
        return impactAnalysisBatchRunner.executeUploads(impactAnalysisBatch);
    }

}
