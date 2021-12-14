package com.synopsys.integration.detectable.detectables.ivy.task;

import java.util.List;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.util.DependencyHistory;

public class IvyDependencyTreeParser {
    private static final String DEPENDENCY_TREE_INDICATOR = "Dependency tree for";
    private static final String DEPENDENCY_TREE_PREFIX = "[ivy:dependencytree] ";
    private static final String ORG_DELIMETER = "#";
    private static final String NAME_VERSION_DELIMETER = ";";
    private static final String[] TREE_LEVEL_TERMINALS = new String[] { "+-", "\\-" };
    private static final int CONSTANT_DELIMETER = 3; // TODO- name better

    private final ExternalIdFactory externalIdFactory;

    public IvyDependencyTreeParser(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph parse(List<String> dependencytreeOutput) {
        MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();

        parseLines(dependencytreeOutput, dependencyGraph);

        return dependencyGraph; //TODO- validate graph with tests
    }

    private void parseLines(List<String> lines, MutableDependencyGraph dependencyGraph) {
        DependencyHistory dependencyHistory = new DependencyHistory();
        boolean parsingDependencytreeOutput = false;
        for (String line : lines) {

            if (line.contains(DEPENDENCY_TREE_INDICATOR)) {
                parsingDependencytreeOutput = true;
                continue;
            }
            if (!parsingDependencytreeOutput) {
                continue;
            }

            line = line.replace(DEPENDENCY_TREE_PREFIX, "");
            int depth = calculateDepth(line);
            if (depth == -1) {
                // non-graph line
                continue;
            }
            dependencyHistory.clearDependenciesDeeperThan(depth);

            Dependency dependency = parseDependencyFromlLine(line);
            if (dependencyHistory.isEmpty()) {
                dependencyGraph.addChildToRoot(dependency);
            } else {
                dependencyGraph.addChildWithParent(dependency, dependencyHistory.getLastDependency());
            }
            dependencyHistory.add(dependency);

        }
    }

    private Dependency createDependency(String org, String name, String version) {
        ExternalId externalIdChild = externalIdFactory.createMavenExternalId(org, name, version);
        return new Dependency(name, version, externalIdChild);
    }

    private Dependency parseDependencyFromlLine(String line) {
        // <...> <org>l#<name>;<version>
        String[] linePieces = line.split(" ");
        String componentInfoStr = linePieces[linePieces.length - 1];

        String[] componentInfoPieces = componentInfoStr.split(ORG_DELIMETER);
        String org = componentInfoPieces[0];
        String nameVersion = componentInfoPieces[1];

        String[] nameVersionPieces = nameVersion.split(NAME_VERSION_DELIMETER);
        String name = nameVersionPieces[0];
        String version = nameVersionPieces[1];

        return createDependency(org, name, version);
    }

    //TODO- make more accurate
    private int calculateDepth(String line) {
        for (String terminal : TREE_LEVEL_TERMINALS) {
            // Format of line is: <...> <terminal> <componentInfo>
            if (line.contains(terminal)) {
                return line.indexOf(terminal) / CONSTANT_DELIMETER;
            }
        }
        return -1;
    }
}
