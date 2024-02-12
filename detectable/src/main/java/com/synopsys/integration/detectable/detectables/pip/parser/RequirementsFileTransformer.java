package com.synopsys.integration.detectable.detectables.pip.parser;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class RequirementsFileTransformer {
    private final RequirementsFileDependencyVersionParser requirementsFileDependencyVersionParser;
    public RequirementsFileTransformer(
        RequirementsFileDependencyVersionParser requirementsFileDependencyVersionParser
    ) {
        this.requirementsFileDependencyVersionParser = requirementsFileDependencyVersionParser;
    }

    public List<RequirementsFileDependency> transform(File requirementsFileObject) {
        List<RequirementsFileDependency> dependencies = new LinkedList<>();

        RequirementsFileDependency requirementsFileDependency = new RequirementsFileDependency("placeholder_dependency", "placeholder_version");
        for (Integer i = 0; i < 10; i++) {
            dependencies.add(requirementsFileDependency);
        }
        return dependencies;
    }


    private List<RequirementsFileDependency> convertEntriesToDependencyInfo(Map<String, RequirementsFileDependencyEntry> dependencyEntries) {
        return dependencyEntries.entrySet().stream()
            .map(entry -> new RequirementsFileDependency(entry.getKey(), requirementsFileDependencyVersionParser.parseRawVersion(entry.getValue().version)))
            .collect(Collectors.toList());
    }
}
