package com.synopsys.integration.detectable.detectables.nuget.future;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.common.util.Bds;

public class SolutionParser {
    public List<ParsedProject> projectsFromSolution(List<String> solutionLines) {
        List<ParsedProject> projects = new ArrayList<>();
        solutionLines.forEach(line -> {
            if (line.startsWith("Project(")) {
                parseProjectLine(line).ifPresent(projects::add);
            }
        });
        return projects;
    }

    public Optional<ParsedProject> parseProjectLine(String line) {
        List<String> pieces = Bds.of(line.split("="))
            .map(StringUtils::trim)
            .filter(StringUtils::isNotBlank)
            .toList();

        if (pieces.size() < 2)
            return Optional.empty();

        String guid = null;
        String name = null;
        String path = null;

        String leftSide = pieces.get(0);
        String rightSide = pieces.get(1);
        if (leftSide.startsWith("Project(\"") && leftSide.endsWith("\")")) {
            guid = middleOfString(leftSide, "Project(\"".length(), "\")".length());
        }
        List<String> opts = Bds.of(rightSide.split(",")).map(StringUtils::trim).toList();
        if (opts.size() >= 1)
            name = middleOfString(opts.get(0), 1, 1); //strip quotes
        if (opts.size() >= 2)
            path = middleOfString(opts.get(1), 1, 1); //strip quotes
        if (opts.size() >= 3)
            guid = middleOfString(opts.get(2), 1, 1); //strip quotes

        return Optional.of(new ParsedProject(path, guid, name));
    }

    private String middleOfString(String source, int fromLeft, int fromRight) {
        String left = source.substring(fromLeft);
        return left.substring(0, left.length() - fromRight);
    }
}