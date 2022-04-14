package com.synopsys.integration.detect.testutils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.BdioId;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.util.NameVersion;

public class DependencyGraphSummarizer {
    private final Gson gson;

    public DependencyGraphSummarizer(Gson gson) {
        this.gson = gson;
    }

    public GraphSummary fromJson(String data) {
        return gson.fromJson(data, GraphSummary.class);
    }

    public String toJson(GraphSummary data) {
        return gson.toJson(data);
    }

    public String toJson(DependencyGraph graph) {
        return toJson(fromGraph(graph));
    }

    public GraphSummary fromGraph(DependencyGraph graph) {
        Queue<Dependency> unprocessed = new LinkedList<>(graph.getRootDependencies());
        Set<Dependency> processed = new HashSet<>();

        GraphSummary graphSummary = new GraphSummary();

        while (unprocessed.size() > 0) {
            Dependency nextDependency = unprocessed.remove();
            processed.add(nextDependency);

            BdioId nextId = nextDependency.getExternalId().createBdioId();
            if (!graphSummary.dependencySummaries.containsKey(nextId)) {
                NameVersion nameVersion = new NameVersion();
                nameVersion.setName(nextDependency.getName());
                nameVersion.setVersion(nextDependency.getVersion());
                graphSummary.dependencySummaries.put(nextId, nameVersion);
            }

            for (Dependency dep : graph.getChildrenForParent(nextDependency)) {
                if (!graphSummary.externalDataIdRelationships.containsKey(nextId)) {
                    graphSummary.externalDataIdRelationships.put(nextId, new HashSet<>());
                }
                graphSummary.externalDataIdRelationships.get(nextId).add(dep.getExternalId().createBdioId());
                if (!processed.contains(dep)) {
                    unprocessed.add(dep);
                }
            }
        }

        graph.getRootDependencies().stream()
            .map(Dependency::getExternalId)
            .map(ExternalId::createBdioId)
            .forEach(graphSummary.rootExternalDataIds::add);

        return graphSummary;
    }

}
