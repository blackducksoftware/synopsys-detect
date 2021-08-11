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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;

public class AggregateModeDirectOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SimpleBdioFactory simpleBdioFactory;

    public AggregateModeDirectOperation(final SimpleBdioFactory simpleBdioFactory) {
        this.simpleBdioFactory = simpleBdioFactory;
    }

    public DependencyGraph aggregateCodeLocations(final File sourcePath, final List<DetectCodeLocation> codeLocations) throws DetectUserFriendlyException {
        final MutableDependencyGraph aggregateDependencyGraph = simpleBdioFactory.createMutableDependencyGraph();

        for (final DetectCodeLocation detectCodeLocation : codeLocations) {
            aggregateDependencyGraph.addGraphAsChildrenToRoot(detectCodeLocation.getDependencyGraph());
        }

        return aggregateDependencyGraph;
    }
}
