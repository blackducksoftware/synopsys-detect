package com.synopsys.integration.detect.workflow.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;

public class CodeLocationDependencyCounter {
    public Map<String, Integer> aggregateCountsByCreatorName(Map<DetectCodeLocation, Integer> codeLocations) {
        Map<String, Integer> dependencyCounts = new HashMap<>();
        for (Map.Entry<DetectCodeLocation, Integer> countEntry : codeLocations.entrySet()) {
            Optional<String> group = countEntry.getKey().getCreatorName();

            if (group.isPresent()) {
                if (!dependencyCounts.containsKey(group.get())) {
                    dependencyCounts.put(group.get(), 0);
                }
                dependencyCounts.put(group.get(), dependencyCounts.get(group.get()) + countEntry.getValue());
            }
        }
        return dependencyCounts;
    }

    public Map<DetectCodeLocation, Integer> countCodeLocations(Set<DetectCodeLocation> codeLocations) {
        Map<DetectCodeLocation, Integer> dependencyCounts = new HashMap<>();
        for (DetectCodeLocation codeLocation : codeLocations) {
            if (!dependencyCounts.containsKey(codeLocation)) {
                dependencyCounts.put(codeLocation, 0);
            }
            dependencyCounts.put(codeLocation, dependencyCounts.get(codeLocation) + countCodeLocationDependencies(codeLocation));
        }
        return dependencyCounts;
    }

    private int countCodeLocationDependencies(DetectCodeLocation codeLocation) {
        Set<ExternalId> rootDependencies = codeLocation.getDependencyGraph().getRootDependencies().stream()
            .map(Dependency::getExternalId)
            .collect(Collectors.toSet());
        return countDependencies(new ArrayList<>(), rootDependencies, codeLocation.getDependencyGraph());
    }

    private int countDependencies(List<ExternalId> processed, Set<ExternalId> remaining, DependencyGraph graph) {
        int sum = 0;
        for (ExternalId dependency : remaining) {
            if (processed.contains(dependency)) {
                continue;
            }
            processed.add(dependency);
            sum += 1 + countDependencies(processed, graph.getChildrenExternalIdsForParent(dependency), graph);
        }
        return sum;
    }
}
