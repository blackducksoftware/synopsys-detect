/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.bdio;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//TODO: Move inside Bdio options?
public class AggregateDecision {

    @Nullable
    private String aggregateName;
    private AggregateMode aggregateMode;
    private boolean uploadEmptyAggregate;

    public AggregateDecision(@Nullable final String aggregateName, final AggregateMode aggregateMode, final boolean uploadEmptyAggregate) {
        this.aggregateName = aggregateName;
        this.aggregateMode = aggregateMode;
        this.uploadEmptyAggregate = uploadEmptyAggregate;
    }

    public static AggregateDecision doNotAggregate() {
        return new AggregateDecision(null, AggregateMode.TRANSITIVE, false);
    }

    public static AggregateDecision aggregateAndAlwaysUpload(@NotNull String aggregateName, @NotNull AggregateMode aggregateMode) {
        return new AggregateDecision(aggregateName, aggregateMode, true);
    }

    public static AggregateDecision aggregateButSkipEmpty(@NotNull String aggregateName, @NotNull AggregateMode aggregateMode) {
        return new AggregateDecision(aggregateName, aggregateMode, false);
    }

    public Optional<String> getAggregateName() {
        return Optional.ofNullable(aggregateName);
    }

    public AggregateMode getAggregateMode() {
        return aggregateMode;
    }

    public boolean shouldUploadEmptyAggregate() {
        return uploadEmptyAggregate;
    }

    public boolean shouldAggregate() {
        return getAggregateName().isPresent();
    }
}

