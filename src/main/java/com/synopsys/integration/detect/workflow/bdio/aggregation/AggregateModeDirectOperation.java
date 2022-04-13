package com.synopsys.integration.detect.workflow.bdio.aggregation;

import java.util.List;
import java.util.Set;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;

public class AggregateModeDirectOperation {
    public DependencyGraph aggregateCodeLocations(List<DetectCodeLocation> codeLocations) throws DetectUserFriendlyException {
        DependencyGraph aggregateDependencyGraph = new BasicDependencyGraph();
        codeLocations.stream()
            .map(DetectCodeLocation::getDependencyGraph)
            .map(DependencyGraph::getDirectDependencies)
            .flatMap(Set::stream)
            .forEach(aggregateDependencyGraph::addDirectDependency);

        return aggregateDependencyGraph;
    }

}
