package com.synopsys.integration.detectable.detectables.bazel;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BazelWorkspaceFileParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Set<WorkspaceRule> parseWorkspaceRuleTypes(List<String> workspaceFileLines) {
        return workspaceFileLines.stream()
            .flatMap(this::parseDependencyRulesFromWorkspaceFileLine)
            .collect(Collectors.toSet());
    }

    // Sonar deems peek useful for debugging.
    private Stream<WorkspaceRule> parseDependencyRulesFromWorkspaceFileLine(String workspaceFileLine) {
        return Arrays.stream(WorkspaceRule.values())
            .filter(workspaceRule -> workspaceFileLine.matches(String.format("^\\s*%s\\s*\\(", workspaceRule.getName())))
            .peek(workspaceRule -> logger.debug(String.format("Found workspace dependency rule: %s", workspaceRule.getName())));
    }
}
