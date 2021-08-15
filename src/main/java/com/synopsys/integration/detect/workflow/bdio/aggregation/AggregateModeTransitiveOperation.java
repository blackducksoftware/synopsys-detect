/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.bdio.aggregation;

import java.io.File;
import java.util.List;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;

public class AggregateModeTransitiveOperation {

    private final FullAggregateGraphCreator fullAggregateGraphCreator;

    public AggregateModeTransitiveOperation(FullAggregateGraphCreator fullAggregateGraphCreator) {
        this.fullAggregateGraphCreator = fullAggregateGraphCreator;
    }

    public DependencyGraph aggregateCodeLocations(File sourcePath, List<DetectCodeLocation> codeLocations) throws DetectUserFriendlyException {
        return fullAggregateGraphCreator.aggregateCodeLocations(new ProjectAsBdioDependencyCreator(), sourcePath, codeLocations);
    }
}
