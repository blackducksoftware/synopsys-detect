/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.operation.blackduck;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.AggregateOptions;
import com.synopsys.integration.detect.lifecycle.run.operation.input.BdioInput;
import com.synopsys.integration.detect.workflow.bdio.BdioManager;
import com.synopsys.integration.detect.workflow.bdio.BdioOptions;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationEventPublisher;
import com.synopsys.integration.detect.workflow.status.OperationSystem;

public class BdioFileGenerationOperation {
    private final String OPERATION_NAME = "BDIO File Generation";
    private final AggregateOptions aggregateOptions;
    private final BdioOptions bdioOptions;
    private final BdioManager bdioManager;
    private final CodeLocationEventPublisher codeLocationEventPublisher;
    private final OperationSystem operationSystem;

    public BdioFileGenerationOperation(AggregateOptions aggregateOptions, BdioOptions bdioOptions, BdioManager bdioManager, CodeLocationEventPublisher codeLocationEventPublisher, OperationSystem operationSystem) {
        this.aggregateOptions = aggregateOptions;
        this.bdioOptions = bdioOptions;
        this.bdioManager = bdioManager;
        this.codeLocationEventPublisher = codeLocationEventPublisher;
        this.operationSystem = operationSystem;
    }

    public BdioResult execute(BdioInput bdioInput) throws DetectUserFriendlyException {
        try {
            BdioResult bdioResult = bdioManager.createBdioFiles(bdioOptions, bdioInput.getAggregateDecision(), bdioInput.getNameVersion(), bdioInput.getCodeLocations(), aggregateOptions.shouldUseBdio2());
            codeLocationEventPublisher.publishDetectCodeLocationNamesCalculated(bdioResult.getCodeLocationNamesResult());
            operationSystem.completeWithSuccess(OPERATION_NAME);
            return bdioResult;
        } catch (DetectUserFriendlyException ex) {
            operationSystem.completeWithError(OPERATION_NAME, ex.getMessage());
            throw ex;
        }
    }
}
