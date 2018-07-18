package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;

public class CodeLocationDependencyCounter {

    public Map<BomToolGroupType, Integer> aggregateCountsByGroup(final Map<DetectCodeLocation, Integer> codeLocations) {
        final Map<BomToolGroupType, Integer> dependencyCounts = new HashMap<>();
        for (final Entry<DetectCodeLocation, Integer> countEntry : codeLocations.entrySet()) {
            final BomToolGroupType group = countEntry.getKey().getBomToolGroupType();
            if (!dependencyCounts.containsKey(group)) {
                dependencyCounts.put(group, 0);
            }
            dependencyCounts.put(group, dependencyCounts.get(group) + countEntry.getValue());
        }
        return dependencyCounts;
    }

    public Map<DetectCodeLocation, Integer> countCodeLocations(final List<DetectCodeLocation> codeLocations) {
        final Map<DetectCodeLocation, Integer> dependencyCounts = new HashMap<>();
        for (final DetectCodeLocation codeLocation : codeLocations) {
            if (!dependencyCounts.containsKey(codeLocation)) {
                dependencyCounts.put(codeLocation, 0);
            }
            dependencyCounts.put(codeLocation, dependencyCounts.get(codeLocation) + countCodeLocationDependencies(codeLocation));
        }
        return dependencyCounts;
    }

    private int countCodeLocationDependencies(final DetectCodeLocation codeLocation) {
        return countDependencies(new ArrayList<ExternalId>(), codeLocation.getDependencyGraph().getRootDependencyExternalIds(), codeLocation.getDependencyGraph());
    }

    private int countDependencies(final List<ExternalId> processed, final Set<ExternalId> remaining, final DependencyGraph graph) {
        int sum = 0;
        for (final ExternalId dependency : remaining) {
            if (processed.contains(dependency)) {
                continue;
            }
            processed.add(dependency);
            sum += 1 + countDependencies(processed, graph.getChildrenExternalIdsForParent(dependency), graph);
        }
        return sum;
    }
}
