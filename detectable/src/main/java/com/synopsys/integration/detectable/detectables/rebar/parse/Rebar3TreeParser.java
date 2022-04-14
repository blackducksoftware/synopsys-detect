package com.synopsys.integration.detectable.detectables.rebar.parse;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.util.DependencyHistory;
import com.synopsys.integration.detectable.detectables.rebar.model.RebarParseResult;
import com.synopsys.integration.util.NameVersion;

public class Rebar3TreeParser {
    private final Logger logger = LoggerFactory.getLogger(Rebar3TreeParser.class);

    private static final String LAST_DEPENDENCY_CHARACTER = "\u2514";
    private static final String NTH_DEPENDENCY_CHARACTER = "\u251C";
    private static final String HORIZONTAL_SEPARATOR_CHARACTER = "\u2500";
    private static final String INNER_LEVEL_CHARACTER = "\u2502";
    private static final String INNER_LEVEL_PREFIX = INNER_LEVEL_CHARACTER + "  ";
    private static final String OUTER_LEVEL_PREFIX = "   ";
    private static final String PROJECT_IDENTIFIER = "(project app)";

    public RebarParseResult parseRebarTreeOutput(List<String> dependencyTreeOutput) {
        DependencyGraph graph = new BasicDependencyGraph();
        DependencyHistory history = new DependencyHistory();
        Dependency project = null;

        for (String line : dependencyTreeOutput) {
            if (!line.contains(HORIZONTAL_SEPARATOR_CHARACTER)) {
                continue;
            }

            Dependency currentDependency = createDependencyFromLine(line);
            int lineLevel = getDependencyLevelFromLine(line);
            try {
                history.clearDependenciesDeeperThan(lineLevel);
            } catch (IllegalStateException e) {
                logger.warn(String.format("Problem parsing line '%s': %s", line, e.getMessage()));
            }

            if (history.isEmpty() && isProject(line)) {
                project = currentDependency;
            } else if (history.getLastDependency().equals(project)) {
                graph.addChildToRoot(currentDependency);
            } else if (history.isEmpty()) {
                graph.addChildToRoot(currentDependency);
            } else {
                graph.addChildWithParents(currentDependency, history.getLastDependency());
            }

            history.add(currentDependency);
        }

        if (project == null) {
            CodeLocation codeLocation = new CodeLocation(graph);
            return new RebarParseResult(codeLocation);
        } else {
            CodeLocation codeLocation = new CodeLocation(graph, project.getExternalId());
            return new RebarParseResult(new NameVersion(project.getName(), project.getVersion()), codeLocation);
        }
    }

    public Dependency createDependencyFromLine(String line) {
        String nameVersionLine = reduceLineToNameVersion(line);
        String name = nameVersionLine.substring(0, nameVersionLine.lastIndexOf(HORIZONTAL_SEPARATOR_CHARACTER));
        String version = nameVersionLine.substring(nameVersionLine.lastIndexOf(HORIZONTAL_SEPARATOR_CHARACTER) + 1);
        return Dependency.FACTORY.createNameVersionDependency(Forge.HEX, name, version);
    }

    public String reduceLineToNameVersion(String line) {
        List<String> ignoredSpecialCharacters = Arrays.asList(LAST_DEPENDENCY_CHARACTER, NTH_DEPENDENCY_CHARACTER, INNER_LEVEL_CHARACTER);
        for (String specialCharacter : ignoredSpecialCharacters) {
            line = line.replaceAll(specialCharacter, "");
        }

        line = line.replaceFirst(HORIZONTAL_SEPARATOR_CHARACTER, "");

        if (line.endsWith(")")) {
            line = line.substring(0, line.lastIndexOf('('));
        }

        return line.trim();
    }

    public int getDependencyLevelFromLine(String line) {
        int level = 0;
        while (line.startsWith(INNER_LEVEL_PREFIX) || line.startsWith(OUTER_LEVEL_PREFIX)) {
            line = line.substring(3);
            level++;
        }

        return level;
    }

    public boolean isProject(String line) {
        String forgeString = "";
        if (line.endsWith(")")) {
            forgeString = line.substring(line.lastIndexOf('('));
        }

        return PROJECT_IDENTIFIER.equals(forgeString);
    }
}
