package com.synopsys.integration.detect.workflow.bdio.aggregation;

import java.util.List;

import com.synopsys.integration.bdio.SimpleBdioFactory;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;

public class AggregateModeDirectOperation {
    private final SimpleBdioFactory simpleBdioFactory;

    public AggregateModeDirectOperation(SimpleBdioFactory simpleBdioFactory) {
        this.simpleBdioFactory = simpleBdioFactory;
    }

    public DependencyGraph aggregateCodeLocations(List<DetectCodeLocation> codeLocations) throws DetectUserFriendlyException {
        MutableDependencyGraph aggregateDependencyGraph = simpleBdioFactory.createMutableDependencyGraph();

        for (DetectCodeLocation detectCodeLocation : codeLocations) {
            aggregateDependencyGraph.addGraphAsChildrenToRoot(detectCodeLocation.getDependencyGraph());
        }

        return aggregateDependencyGraph;
    }

}
