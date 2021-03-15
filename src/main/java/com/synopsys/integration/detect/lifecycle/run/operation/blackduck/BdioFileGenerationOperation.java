/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.operation.blackduck;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.RunOptions;
import com.synopsys.integration.detect.lifecycle.run.operation.input.BdioInput;
import com.synopsys.integration.detect.workflow.bdio.BdioManager;
import com.synopsys.integration.detect.workflow.bdio.BdioOptions;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationEventPublisher;
import com.synopsys.integration.detect.workflow.status.Operation;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;
import com.synopsys.integration.detect.workflow.status.StatusType;

public class BdioFileGenerationOperation {
    private final String OPERATION_NAME = "BDIO File Generation";
    private final RunOptions runOptions;
    private final BdioOptions bdioOptions;
    private final BdioManager bdioManager;
    private final CodeLocationEventPublisher codeLocationEventPublisher;
    private final StatusEventPublisher statusEventPublisher;

    public BdioFileGenerationOperation(RunOptions runOptions, BdioOptions bdioOptions, BdioManager bdioManager, CodeLocationEventPublisher codeLocationEventPublisher, StatusEventPublisher statusEventPublisher) {
        this.runOptions = runOptions;
        this.bdioOptions = bdioOptions;
        this.bdioManager = bdioManager;
        this.codeLocationEventPublisher = codeLocationEventPublisher;
        this.statusEventPublisher = statusEventPublisher;
    }

    public BdioResult execute(BdioInput bdioInput) throws DetectUserFriendlyException {
        try {
            BdioResult bdioResult = bdioManager.createBdioFiles(bdioOptions, bdioInput.getAggregateOptions(), bdioInput.getNameVersion(), bdioInput.getCodeLocations(), runOptions.shouldUseBdio2());
            codeLocationEventPublisher.publishDetectCodeLocationNamesCalculated(bdioResult.getCodeLocationNamesResult());
            statusEventPublisher.publishOperation(new Operation(OPERATION_NAME, StatusType.SUCCESS));
            return bdioResult;
        } catch (DetectUserFriendlyException ex) {
            statusEventPublisher.publishOperation(new Operation(OPERATION_NAME, StatusType.FAILURE));
            throw ex;
        }
    }
}
