/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.operation.input;

import java.util.List;

import com.synopsys.integration.detect.workflow.bdio.AggregateOptions;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.util.NameVersion;

public class BdioInput {
    private final AggregateOptions aggregateOptions;
    private final NameVersion nameVersion;
    private final List<DetectCodeLocation> codeLocations;

    public BdioInput(AggregateOptions aggregateOptions, NameVersion nameVersion, List<DetectCodeLocation> codeLocations) {
        this.aggregateOptions = aggregateOptions;
        this.nameVersion = nameVersion;
        this.codeLocations = codeLocations;
    }

    public AggregateOptions getAggregateOptions() {
        return aggregateOptions;
    }

    public NameVersion getNameVersion() {
        return nameVersion;
    }

    public List<DetectCodeLocation> getCodeLocations() {
        return codeLocations;
    }
}
