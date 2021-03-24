/**
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
import com.synopsys.integration.detect.workflow.bdio.AggregateMode;
import com.synopsys.integration.detect.workflow.bdio.AggregateOptions;
import com.synopsys.integration.exception.IntegrationException;

public class AggregateOptionsOperation {
    private final RunOptions runOptions;

    public AggregateOptionsOperation(RunOptions runOptions) {
        this.runOptions = runOptions;
    }

    public AggregateOptions execute(Boolean anythingFailedPrior) throws DetectUserFriendlyException, IntegrationException {
        String aggregateName = runOptions.getAggregateName().orElse(null);
        AggregateMode aggregateMode = runOptions.getAggregateMode();
        AggregateOptions aggregateOptions;
        if (StringUtils.isNotBlank(aggregateName)) {
            if (anythingFailedPrior.booleanValue()) {
                aggregateOptions = AggregateOptions.aggregateButSkipEmpty(aggregateName, aggregateMode);
            } else {
                aggregateOptions = AggregateOptions.aggregateAndAlwaysUpload(aggregateName, aggregateMode);
            }
        } else {
            aggregateOptions = AggregateOptions.doNotAggregate();
        }

        return aggregateOptions;
    }
}
