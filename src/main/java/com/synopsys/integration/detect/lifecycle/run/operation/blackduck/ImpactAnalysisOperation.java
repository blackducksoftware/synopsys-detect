/**
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
package com.synopsys.integration.detect.lifecycle.run.operation.blackduck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.operation.input.ImpactAnalysisInput;
import com.synopsys.integration.detect.tool.impactanalysis.BlackDuckImpactAnalysisTool;
import com.synopsys.integration.detect.tool.impactanalysis.ImpactAnalysisToolResult;
import com.synopsys.integration.exception.IntegrationException;

public class ImpactAnalysisOperation {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final BlackDuckImpactAnalysisTool blackDuckImpactAnalysisTool;

    public ImpactAnalysisOperation(BlackDuckImpactAnalysisTool blackDuckImpactAnalysisTool) {
        this.blackDuckImpactAnalysisTool = blackDuckImpactAnalysisTool;
    }

    public ImpactAnalysisToolResult execute(ImpactAnalysisInput impactAnalysisInput) throws DetectUserFriendlyException, IntegrationException {
        ImpactAnalysisToolResult impactAnalysisToolResult = blackDuckImpactAnalysisTool.performImpactAnalysisActions(impactAnalysisInput.getProjectNameVersion(), impactAnalysisInput.getProjectVersionWrapper());

        if (impactAnalysisToolResult.isSuccessful()) {
            logger.info("Vulnerability Impact Analysis successful.");
        } else {
            logger.warn("Something went wrong with the Vulnerability Impact Analysis tool.");
        }

        return impactAnalysisToolResult;
    }

    public boolean shouldImpactAnalysisToolRun() {
        return blackDuckImpactAnalysisTool.shouldRun();
    }
}
