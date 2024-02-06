package com.synopsys.integration.detectable.detectables.pip.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

    public List<RequirementsFileDependency> transform(File requirementsFileObject) throws IOException {

        List<RequirementsFileDependency> dependencies = new LinkedList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(requirementsFileObject))) {
            for (String line; (line = bufferedReader.readLine()) != null; ) {
                // Ignore comments (i.e. lines starting with #) and empty/whitespace lines
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                List<String> tokens = Arrays.asList(line.trim().split(" "));

                int operatorIndex = tokens.indexOf("==");


                int index = findSeparatorIndex(line);
                if (index != -1) {
                    String dependency = line.substring(0, index).trim();
                    String version = line.substring(index + 1).trim();
                    RequirementsFileDependency requirementsFileDependency = new RequirementsFileDependency(dependency, version);
                    dependencies.add(requirementsFileDependency);
                }

            }
        }

//        RequirementsFileDependency requirementsFileDependency = new RequirementsFileDependency("placeholder_dependency", "placeholder_version");
//        for (Integer i = 0; i < 10; i++) {
//            dependencies.add(requirementsFileDependency);
//        }
        return dependencies;
    }

    private int findSeparatorIndex(String line) {
        return line.indexOf("==");
    }



    private List<RequirementsFileDependency> convertEntriesToDependencyInfo(Map<String, RequirementsFileDependencyEntry> dependencyEntries) {
        return dependencyEntries.entrySet().stream()
            .map(entry -> new RequirementsFileDependency(entry.getKey(), requirementsFileDependencyVersionParser.parseRawVersion(entry.getValue().version)))
            .collect(Collectors.toList());
    }
}
