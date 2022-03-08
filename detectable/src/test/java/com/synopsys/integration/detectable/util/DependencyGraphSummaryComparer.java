package com.synopsys.integration.detectable.util;

import java.util.Set;
import java.util.stream.Collectors;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.BdioId;

public class DependencyGraphSummaryComparer {
    private final DependencyGraphSummarizer dependencyGraphSummarizer;

    public DependencyGraphSummaryComparer(DependencyGraphSummarizer dependencyGraphSummarizer) {
        this.dependencyGraphSummarizer = dependencyGraphSummarizer;
    }

    public boolean areEqual(DependencyGraph left, DependencyGraph right) {
        GraphSummary leftSummary = dependencyGraphSummarizer.fromGraph(left);
        GraphSummary rightSummary = dependencyGraphSummarizer.fromGraph(right);
        return areEqual(leftSummary, rightSummary);
    }

    public boolean areEqual(GraphSummary left, GraphSummary right) {
        boolean isEqual = true;

        isEqual = isEqual && left.rootExternalDataIds.equals(right.rootExternalDataIds);
        isEqual = isEqual && left.dependencySummaries.keySet().equals(right.dependencySummaries.keySet());

        Set<BdioId> leftRelationshipIds = left.externalDataIdRelationships.keySet();
        Set<BdioId> leftExistingRelationshipsIds = leftRelationshipIds.stream()
            .filter(key -> left.externalDataIdRelationships.get(key) != null && left.externalDataIdRelationships.get(key).size() > 0).collect(Collectors.toSet());

        Set<BdioId> rightRelationshipIds = right.externalDataIdRelationships.keySet();
        Set<BdioId> rightExistingRelationshipsIds = rightRelationshipIds.stream()
            .filter(key -> right.externalDataIdRelationships.get(key) != null && right.externalDataIdRelationships.get(key).size() > 0).collect(Collectors.toSet());

        isEqual = isEqual && leftExistingRelationshipsIds.equals(rightExistingRelationshipsIds);

        for (BdioId key : left.dependencySummaries.keySet()) {
            isEqual = isEqual && left.dependencySummaries.get(key).getName().equals(right.dependencySummaries.get(key).getName());
            isEqual = isEqual && left.dependencySummaries.get(key).getVersion().equals(right.dependencySummaries.get(key).getVersion());
        }
        for (BdioId key : leftExistingRelationshipsIds) {
            isEqual = isEqual && left.externalDataIdRelationships.get(key).equals(right.externalDataIdRelationships.get(key));
        }

        return isEqual;
    }

}
