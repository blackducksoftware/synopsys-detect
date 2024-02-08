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


    private static final List<String> OPERATORS_IN_PRIORITY_ORDER = Arrays.asList("==", "=", ">=", "~=", "<=", ">", "<");
    private static final List<String> IGNORE_AFTER_CHARACTERS = Arrays.asList("#", ";", ",");
    private static final List<String> TOKEN_CLEANUP_CHARS = Arrays.asList("==", ",", "\"");
    public RequirementsFileTransformer(
        RequirementsFileDependencyVersionParser requirementsFileDependencyVersionParser
    ) {
        this.requirementsFileDependencyVersionParser = requirementsFileDependencyVersionParser;
    }
    public List<RequirementsFileDependency> transform(File requirementsFileObject) throws IOException {

        List<RequirementsFileDependency> dependencies = new LinkedList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(requirementsFileObject))) {
            for (String line; (line = bufferedReader.readLine()) != null; ) {

                String formattedLine = formatLine(line);

                // Ignore comments (i.e. lines starting with #) and empty/whitespace lines.
                if (formattedLine.isEmpty() || formattedLine.startsWith("#")) {
                    continue;
                }

                List<String> tokens = Arrays.asList(line.trim().split(" "));

                // Extract dependency. This will always be the first token of each valid line.
                String dependency = "";
                if (!tokens.isEmpty()) {
                    dependency = formatToken(tokens.get(0));
                }

                // Find the index of the operator that separates a dependency from its version.
                // Extract version. Version extracted will be the next token after operator.
                int operatorIndex = findOperatorIndex(tokens);
                String version = "";
                if (operatorIndex > 0 && operatorIndex < tokens.size() - 1) {
                    version = formatToken(tokens.get(operatorIndex + 1));
                }

                // Create a dependency entry and add it to the list
                if (!dependency.isEmpty()) {
                    RequirementsFileDependency requirementsFileDependency = new RequirementsFileDependency(dependency, version);
                    dependencies.add(requirementsFileDependency);
                }
            }
        }
        return dependencies;
    }

    private int findOperatorIndex(List<String> tokens) {
        int operatorIndex = -1;
        for (String operator : OPERATORS_IN_PRIORITY_ORDER) {
            operatorIndex = tokens.indexOf(operator);
            if (operatorIndex != -1) {
                return operatorIndex;
            }
        }
        return operatorIndex;
    }

    private List<String> splitByOperator(String line) {
        for (String operator : OPERATORS_IN_PRIORITY_ORDER) {
            if (line.contains(operator)) {
                return Arrays.asList(line.trim().split(operator));
            }
        }
        return Arrays.asList(line.trim().split(" "));
    }

    private String formatLine(String line) {
        int ignoreAfterIndex;
        String formattedLine = line.trim();
        for (String ignoreAfterChar : IGNORE_AFTER_CHARACTERS) {
            ignoreAfterIndex = formattedLine.indexOf(ignoreAfterChar);
            formattedLine = formattedLine.substring(0, ignoreAfterIndex);
        }
        return formattedLine;
    }

    private String formatToken(String token) {
        // Clean up any irrelevant symbols/chars from token
        for (String charToRemove : TOKEN_CLEANUP_CHARS) {
            token = token.replace(charToRemove, "");
        }

        // Remove any strings in square brackets. For example, if token is requests["foo", "bar"], it should be cleaned up to show as "requests"
        int bracketIndex = token.indexOf("[");
        if (bracketIndex > 0) {
            token = token.substring(0, bracketIndex);
        }
        return token;
    }
}
