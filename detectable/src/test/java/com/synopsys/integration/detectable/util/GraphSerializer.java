package com.synopsys.integration.detectable.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class GraphSerializer {
    private static final Logger logger = LoggerFactory.getLogger(GraphSerializer.class);

    public static String serialize(DependencyGraph graph) {

        StringBuilder rootText = new StringBuilder();
        for (Dependency dependency : graph.getRootDependencies()) {
            rootText.append("\t" + dependencyToString(dependency) + "\n");
        }

        StringBuilder relationshipText = new StringBuilder();
        Set<Dependency> processed = new HashSet<>();
        Stack<Dependency> unprocessed = new Stack<>();
        unprocessed.addAll(graph.getRootDependencies());

        while (unprocessed.size() > 0) {
            Dependency dependency = unprocessed.pop();
            if (processed.contains(dependency)) {
                continue;
            } else {
                processed.add(dependency);
            }
            StringBuilder childText = new StringBuilder();
            int cnt = 0;
            for (Dependency childDependency : graph.getChildrenForParent(dependency)) {
                cnt++;
                unprocessed.add(childDependency);
                childText.append("\t\t" + dependencyToString(childDependency) + "\n");
            }
            if (cnt > 0) {
                relationshipText.append("\t" + dependencyToString(dependency) + "\n");
                relationshipText.append(childText.toString());
            }
        }

        StringBuilder graphText = new StringBuilder();
        graphText.append("Detect Graph v0.0.0" + "\n");
        graphText.append("\tRoot Dependencies (" + graph.getRootDependencies().size() + ")" + "\n");
        graphText.append("\tTotal Dependencies (" + graph.getRootDependencies().size() + ")" + "\n");
        graphText.append("Root Dependencies" + "\n");
        graphText.append(rootText.toString());
        graphText.append("Relationships" + "\n");
        graphText.append(relationshipText.toString());
        graphText.append("\n");

        logger.info(graphText.toString());
        return graphText.toString();
    }

    private static String dependencyToString(Dependency dependency) {
        return dependency.getName() + "," + dependency.getVersion() + "," + externalIdToString(dependency.getExternalId());
    }

    private static String escape(String target) {
        return target.replaceAll(",", "%comma%");
    }

    private static String externalIdToString(ExternalId externalId) {
        String forge = externalId.getForge().getName().toString();
        String pieces = Arrays.stream(externalId.getExternalIdPieces()).map(GraphSerializer::escape).collect(Collectors.joining(","));
        return forge + "," + pieces;
    }
}
