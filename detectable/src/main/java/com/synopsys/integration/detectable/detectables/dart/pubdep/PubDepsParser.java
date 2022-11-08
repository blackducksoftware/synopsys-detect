package com.synopsys.integration.detectable.detectables.dart.pubdep;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.detectable.detectable.util.DependencyHistory;

public class PubDepsParser {
    private static final String UNRESOLVED_VERSION_SUFFIX = "...";

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
        int depth = StringUtils.countMatches(line, "|");
        if (line.contains("'--")) {
            // |   '-- <name>...
            depth += StringUtils.countMatches(line, "'");
        }
        if (!line.equals(line.trim()) && line.trim().startsWith("'--")) {
            //    '-- collection...
            depth += 1;
        }
        return depth;
    }

}
