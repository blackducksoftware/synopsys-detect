package com.synopsys.integration.detectable.detectables.pip.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

                String dependency = "";
                if (!tokens.isEmpty()) {
                    dependency = tokens.get(0);
                }

                int operatorIndex = tokens.indexOf("==");
                String version = "";
                if (operatorIndex > 0 && operatorIndex < tokens.size() - 1) {
                    version = tokens.get(operatorIndex + 1);
                }

                if (!dependency.isEmpty()) {
                    RequirementsFileDependency requirementsFileDependency = new RequirementsFileDependency(dependency, version);
                    dependencies.add(requirementsFileDependency);
                }
            }
        }
        return dependencies;
    }


}
