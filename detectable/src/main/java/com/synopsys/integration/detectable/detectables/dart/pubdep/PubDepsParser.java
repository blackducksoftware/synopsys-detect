package com.synopsys.integration.detectable.detectables.dart.pubdep;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.detectable.detectable.util.DependencyHistory;

public class PubDepsParser {
    private static final String UNRESOLVED_VERSION_SUFFIX = "...";

    // starting with Dart 2.19 the dependency graph contains UTF-8 characters instead of ASCII-only
    // the following are some of the characters that are used and are required for parsing of the graph
    private static final String UTF8_BOX_UP_AND_RIGHT       = "\u2514"; // └
    private static final String UTF8_BOX_VERTICAL_AND_RIGHT = "\u251c"; // ├

    private static final String[] DEPENDENCY_LINE_INDICATORS = {
        "|--", "'--", UTF8_BOX_UP_AND_RIGHT, UTF8_BOX_VERTICAL_AND_RIGHT
    };

    private static final int LEVEL_INDENTATION_WIDTH = 4;

    public DependencyGraph parse(List<String> pubDepsOutput) {
        DependencyGraph dependencyGraph = new BasicDependencyGraph();

        Map<String, String> resolvedVersions = resolveVersionsOfDependencies(pubDepsOutput);

        parseLines(pubDepsOutput, resolvedVersions, dependencyGraph);

        return dependencyGraph;
    }

    private void parseLines(List<String> lines, Map<String, String> resolvedVersions, DependencyGraph dependencyGraph) {
        DependencyHistory dependencyHistory = new DependencyHistory();
        for (String line : lines) {
            int depthOfLine = calculateDepth(line);
            if (depthOfLine == 0) {
                // non-graph line
                continue;
            }
            int dependencyDepth = depthOfLine - 1;
            dependencyHistory.clearDependenciesDeeperThan(dependencyDepth);

            String nameOfDependency = parseNameFromlLine(line);
            Dependency dependency = createDependency(nameOfDependency, resolvedVersions);
            if (dependencyHistory.isEmpty()) {
                dependencyGraph.addChildToRoot(dependency);
            } else {
                dependencyGraph.addChildWithParent(dependency, dependencyHistory.getLastDependency());
            }
            dependencyHistory.add(dependency);

        }
    }

    private Dependency createDependency(String name, Map<String, String> resolvedVersions) {
        String version = resolvedVersions.get(name);
        return Dependency.FACTORY.createNameVersionDependency(Forge.DART, name, version);
    }

    private Map<String, String> resolveVersionsOfDependencies(List<String> pubDepsOutput) {
        Map<String, String> resolvedVersionsOfDependencies = new HashMap<>();
        for (String line : pubDepsOutput) {
            if (!line.endsWith("...")) {
                // <...> |-- <name> <version>
                String[] pieces = line.split(" ");
                String name = pieces[pieces.length - 2];
                String vesion = pieces[pieces.length - 1];
                resolvedVersionsOfDependencies.put(name, vesion);
            }
        }
        return resolvedVersionsOfDependencies;
    }

    private String parseNameFromlLine(String line) {
        String[] pieces = line.split(" ");
        if (line.endsWith(UNRESOLVED_VERSION_SUFFIX)) {
            // <...> <name>...
            String nameWithSuffix = pieces[pieces.length - 1];
            return nameWithSuffix.substring(0, nameWithSuffix.length() - UNRESOLVED_VERSION_SUFFIX.length());
        } else {
            // <...> <name> <version>
            return pieces[pieces.length - 2];
        }
    }

    private int calculateDepth(String line) {
        int indicatorPosition = -1;
        for (String indicator : DEPENDENCY_LINE_INDICATORS) {
            int index = line.indexOf(indicator);
            if (index == -1)
                continue;
            indicatorPosition = index;
        }

        if (indicatorPosition == -1)
            return 0;
        return indicatorPosition / LEVEL_INDENTATION_WIDTH + 1;
    }

}
