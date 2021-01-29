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
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationResult;
import com.synopsys.integration.detect.lifecycle.run.operation.ToolOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.input.ImpactAnalysisInput;
import com.synopsys.integration.detect.tool.impactanalysis.BlackDuckImpactAnalysisTool;
import com.synopsys.integration.detect.tool.impactanalysis.ImpactAnalysisToolResult;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationAccumulator;
import com.synopsys.integration.exception.IntegrationException;

public class ImpactAnalysisOperation extends ToolOperation<ImpactAnalysisInput, CodeLocationAccumulator> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private DetectToolFilter detectToolFilter;
    private final BlackDuckImpactAnalysisTool blackDuckImpactAnalysisTool;

    public ImpactAnalysisOperation(DetectToolFilter detectToolFilter, BlackDuckImpactAnalysisTool blackDuckImpactAnalysisTool) {
        this.detectToolFilter = detectToolFilter;
        this.blackDuckImpactAnalysisTool = blackDuckImpactAnalysisTool;
    }

    @Override
    protected boolean shouldExecute() {
        return detectToolFilter.shouldInclude(DetectTool.IMPACT_ANALYSIS) && blackDuckImpactAnalysisTool.shouldRun();
    }

    @Override
    public String getOperationName() {
        return "Vulnerability Impact Analysis";
    }

    @Override
    protected OperationResult<CodeLocationAccumulator> executeOperation(ImpactAnalysisInput input) throws DetectUserFriendlyException, IntegrationException {
        ImpactAnalysisToolResult impactAnalysisToolResult = blackDuckImpactAnalysisTool.performImpactAnalysisActions(input.getNameVersion(), input.getProjectVersionWrapper());

        /* TODO: There is currently no mechanism within Black Duck for checking the completion status of an Impact Analysis code location. Waiting should happen here when such a mechanism exists. See HUB-25142. JM - 08/2020 */
        input.getCodeLocationAccumulator().addNonWaitableCodeLocation(impactAnalysisToolResult.getCodeLocationNames());

        OperationResult result;
        if (impactAnalysisToolResult.isSuccessful()) {
            logger.info("Vulnerability Impact Analysis successful.");
            result = OperationResult.success(input.getCodeLocationAccumulator());
        } else {
            logger.warn("Something went wrong with the Vulnerability Impact Analysis tool.");
            result = OperationResult.fail(input.getCodeLocationAccumulator());
        }
        return result;
    }
}
