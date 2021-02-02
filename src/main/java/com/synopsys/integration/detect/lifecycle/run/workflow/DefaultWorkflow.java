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

import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadBatchOutput;
import com.synopsys.integration.blackduck.codelocation.bdioupload.UploadOutput;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.RunResult;
import com.synopsys.integration.detect.lifecycle.run.operation.BazelOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.DetectorOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.DockerOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationFactory;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationResult;
import com.synopsys.integration.detect.lifecycle.run.operation.PolarisOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.AggregateOptionsOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.BdioFileGenerationOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.BinaryScanOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.CodeLocationOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.CodeLocationResultOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.FullScanPostProcessingOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.ImpactAnalysisOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.ProjectCreationOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.ProjectDecisionOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.blackduck.SignatureScanOperation;
import com.synopsys.integration.detect.lifecycle.run.operation.input.BdioInput;
import com.synopsys.integration.detect.lifecycle.run.operation.input.CodeLocationInput;
import com.synopsys.integration.detect.lifecycle.run.operation.input.FullScanPostProcessingInput;
import com.synopsys.integration.detect.lifecycle.run.operation.input.ImpactAnalysisInput;
import com.synopsys.integration.detect.lifecycle.run.operation.input.SignatureScanInput;
import com.synopsys.integration.detect.workflow.bdio.AggregateOptions;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationAccumulator;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationResults;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class DefaultWorkflow extends Workflow {
    public DefaultWorkflow(PropertyConfiguration detectConfiguration, OperationFactory operationFactory) {
        super(detectConfiguration, operationFactory);
    }

    @Override
    public WorkflowResult executeWorkflow() throws DetectUserFriendlyException, IntegrationException {
        RunResult runResult = new RunResult();
        PolarisOperation polarisOperation = getOperationFactory().createPolarisOperation();
        DockerOperation dockerOperation = getOperationFactory().createDockerOperation();
        BazelOperation bazelOperation = getOperationFactory().createBazelOperation();
        DetectorOperation detectorOperation = getOperationFactory().createDetectorOperation();
        ProjectDecisionOperation projectDecisionOperation = getOperationFactory().createProjectDecisionOperation();
        AggregateOptionsOperation aggregateOptionsOperation = getOperationFactory().createAggregateOptionsOperation();
        ProjectCreationOperation projectCreationOperation = getOperationFactory().createProjectCreationOperation();
        BdioFileGenerationOperation bdioFileGenerationOperation = getOperationFactory().createBdioFileGenerationOperation();
        CodeLocationOperation codeLocationOperation = getOperationFactory().createCodeLocationOperation();
        SignatureScanOperation signatureScanOperation = getOperationFactory().createSignatureScanOperation();
        BinaryScanOperation binaryScanOperation = getOperationFactory().createBinaryScanOperation();
        ImpactAnalysisOperation impactAnalysisOperation = getOperationFactory().createImpactAnalysisOperation();
        CodeLocationResultOperation codeLocationResultOperation = getOperationFactory().createCodeLocationResultOperation();
        FullScanPostProcessingOperation fullScanPostProcessingOperation = getOperationFactory().createFullScanPostProcessingOperation();

        polarisOperation.execute(null);
        OperationResult<Void> dockerResult = dockerOperation.execute(runResult);
        OperationResult<Void> bazelResult = bazelOperation.execute(runResult);
        OperationResult<Void> detectorResult = detectorOperation.execute(runResult);

        boolean priorOperationsSucceeded = dockerResult.hasSucceeded() && bazelResult.hasSucceeded() && detectorResult.hasSucceeded();

        OperationResult<NameVersion> projectInfo = projectDecisionOperation.execute(runResult.getDetectToolProjectInfo());
        NameVersion projectNameVersion = projectInfo.getContent();

        OperationResult<ProjectVersionWrapper> projectCreationResult = projectCreationOperation.execute(projectNameVersion);
        ProjectVersionWrapper projectVersionWrapper = projectCreationResult.getContent();

        OperationResult<AggregateOptions> aggregateOptions = aggregateOptionsOperation.execute(priorOperationsSucceeded);
        BdioInput bdioInput = new BdioInput(aggregateOptions.getContent(), projectNameVersion, runResult.getDetectCodeLocations());

        OperationResult<BdioResult> bdioGeneration = bdioFileGenerationOperation.execute(bdioInput);
        BdioResult bdioResult = bdioGeneration.getContent();

        OperationResult<CodeLocationAccumulator<UploadOutput, UploadBatchOutput>> codeLocationResult = codeLocationOperation.execute(bdioResult);
        CodeLocationAccumulator<UploadOutput, UploadBatchOutput> codeLocationAccumulator = codeLocationResult.getContent();
        SignatureScanInput signatureScanInput = new SignatureScanInput(projectNameVersion, codeLocationAccumulator, runResult.getDockerTar().orElse(null));

        signatureScanOperation.execute(signatureScanInput);
        CodeLocationInput codeLocationInput = new CodeLocationInput(projectNameVersion, codeLocationAccumulator);

        binaryScanOperation.execute(codeLocationInput);
        ImpactAnalysisInput impactAnalysisInput = new ImpactAnalysisInput(projectNameVersion, codeLocationAccumulator, projectVersionWrapper);

        impactAnalysisOperation.execute(impactAnalysisInput);

        OperationResult<CodeLocationResults> codeLocationProcessingResult = codeLocationResultOperation.execute(codeLocationAccumulator);
        FullScanPostProcessingInput postProcessingInput = new FullScanPostProcessingInput(projectNameVersion, bdioResult, codeLocationProcessingResult.getContent(), projectVersionWrapper);
        fullScanPostProcessingOperation.execute(postProcessingInput);
        return WorkflowResult.success();
    }
}
