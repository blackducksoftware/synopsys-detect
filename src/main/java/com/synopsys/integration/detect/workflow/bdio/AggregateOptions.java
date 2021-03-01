/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.bdio;

import java.util.Optional;

//TODO: Move inside Bdio options?
public class AggregateOptions {

    private Optional<String> aggregateName;
    private AggregateMode aggregateMode;
    private boolean uploadEmptyAggregate;

    public AggregateOptions(final Optional<String> aggregateName, final AggregateMode aggregateMode, final boolean uploadEmptyAggregate) {
        this.aggregateName = aggregateName;
        this.aggregateMode = aggregateMode;
        this.uploadEmptyAggregate = uploadEmptyAggregate;
    }

    public static AggregateOptions doNotAggregate() {
        return new AggregateOptions(Optional.empty(), AggregateMode.TRANSITIVE, false);
    }

    public static AggregateOptions aggregateAndAlwaysUpload(String aggregateName, AggregateMode aggregateMode) {
        return new AggregateOptions(Optional.of(aggregateName), aggregateMode, true);
    }

    public static AggregateOptions aggregateButSkipEmpty(String aggregateName, AggregateMode aggregateMode) {
        return new AggregateOptions(Optional.of(aggregateName), aggregateMode, false);
    }

    public Optional<String> getAggregateName() {
        return aggregateName;
    }

    public AggregateMode getAggregateMode() {
        return aggregateMode;
    }

    public boolean shouldUploadEmptyAggregate() {
        return uploadEmptyAggregate;
    }

    public boolean shouldAggregate() {
        return aggregateName.isPresent();
    }
}

