/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.operation.blackduck;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.run.RunOptions;
import com.synopsys.integration.detect.workflow.bdio.AggregateDecision;
import com.synopsys.integration.detect.workflow.bdio.AggregateMode;
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.exception.IntegrationException;

public class AggregateDecisionOperation {
    private static final String OPERATION_NAME = "Detect Aggregate Options Decision";
    private final RunOptions runOptions;
    private final OperationSystem operationSystem;

    public AggregateDecisionOperation(RunOptions runOptions, OperationSystem operationSystem) {
        this.runOptions = runOptions;
        this.operationSystem = operationSystem;
    }

    public AggregateDecision execute(Boolean anythingFailedPrior) throws DetectUserFriendlyException, IntegrationException {
        operationSystem.beginOperation(OPERATION_NAME);
        String aggregateName = runOptions.getAggregateName().orElse(null);
        AggregateMode aggregateMode = runOptions.getAggregateMode();
        AggregateDecision aggregateDecision;
        if (StringUtils.isNotBlank(aggregateName)) {
            if (anythingFailedPrior) {
                aggregateDecision = AggregateDecision.aggregateButSkipEmpty(aggregateName, aggregateMode);
            } else {
                aggregateDecision = AggregateDecision.aggregateAndAlwaysUpload(aggregateName, aggregateMode);
            }
        } else {
            aggregateDecision = AggregateDecision.doNotAggregate();
        }
        operationSystem.completeWithSuccess(OPERATION_NAME);

        return aggregateDecision;
    }
}
