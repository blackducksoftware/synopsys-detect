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
package com.synopsys.integration.detect.lifecycle.run.workflow;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.EventAccumulator;
import com.synopsys.integration.detect.lifecycle.run.RunResult;
import com.synopsys.integration.detect.lifecycle.run.operation.DetectorOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationFactory;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationResult;
import com.synopsys.integration.detect.lifecycle.run.operation.RapidScanOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.AggregateOptionsOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.BdioFileGenerationOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.ProjectDecisionOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.input.BdioInput;
import com.synopsys.integration.detect.lifecycle.run.operation.input.RapidScanInput;
import com.synopsys.integration.detect.workflow.bdio.AggregateOptions;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class RapidScanWorkflow extends Workflow {
    public RapidScanWorkflow(PropertyConfiguration detectConfiguration, OperationFactory operationFactory, EventAccumulator eventAccumulator) {
        super(detectConfiguration, operationFactory, eventAccumulator);
    }

    @Override
    public WorkflowResult executeWorkflow() throws DetectUserFriendlyException, IntegrationException {
        RunResult runResult = new RunResult();
        DetectorOperation detectorOperation = getOperationFactory().createDetectorOperation();
        ProjectDecisionOperation projectDecisionOperation = getOperationFactory().createProjectDecisionOperation();
        AggregateOptionsOperation aggregateOptionsOperation = getOperationFactory().createAggregateOptionsOperation();
        BdioFileGenerationOperation bdioFileGenerationOperation = getOperationFactory().createBdioFileGenerationOperation();
        RapidScanOperation rapidScanOperation = getOperationFactory().createRapidScanOperation();

        OperationResult<RunResult> detectorResult = detectorOperation.execute(runResult);

        OperationResult<NameVersion> projectInfo = projectDecisionOperation.execute(runResult.getDetectToolProjectInfo());
        NameVersion projectNameVersion = projectInfo.getContent();

        OperationResult<AggregateOptions> aggregateOptions = aggregateOptionsOperation.execute(detectorResult.hasFailed());
        BdioInput bdioInput = new BdioInput(aggregateOptions.getContent(), projectNameVersion, runResult.getDetectCodeLocations());

        OperationResult<BdioResult> bdioGeneration = bdioFileGenerationOperation.execute(bdioInput);
        BdioResult bdioResult = bdioGeneration.getContent();

        RapidScanInput rapidScanInput = new RapidScanInput(projectNameVersion, bdioResult);
        rapidScanOperation.execute(rapidScanInput);
        return WorkflowResult.success(getEventAccumulator());
    }
}
