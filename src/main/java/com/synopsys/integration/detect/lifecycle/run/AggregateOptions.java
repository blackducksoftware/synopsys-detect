/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detect.workflow.bdio.AggregateMode;

public class AggregateOptions {
    private final String aggregateName;
    private final AggregateMode aggregateMode;

    public AggregateOptions(@Nullable String aggregateName, AggregateMode aggregateMode) {
        this.aggregateName = aggregateName;
        this.aggregateMode = aggregateMode;
    }

    public Optional<String> getAggregateName() {
        return Optional.ofNullable(aggregateName);
    }

    public AggregateMode getAggregateMode() {
        return aggregateMode;
    }
}
