package com.synopsys.integration.detectable.detectables.setuptools.parse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tomlj.TomlParseResult;

import com.synopsys.integration.detectable.python.util.PythonDependency;
import com.synopsys.integration.detectable.python.util.PythonDependencyTransformer;

public class SetupToolsPyParser implements SetupToolsParser {
    
    private TomlParseResult parsedToml;
    
    private List<String> dependencies;
    
    public SetupToolsPyParser(TomlParseResult parsedToml) {
        this.parsedToml = parsedToml;
        this.dependencies = new ArrayList<>();
    }
    
    @Override
    public SetupToolsParsedResult parse() throws IOException {
        // Use a name from the toml if we have it. Do not parse names and versions from the setup.py
        // as the project will not always have a string (it could have variables or method calls)
        String tomlProjectName = parsedToml.getString("project.name");
        String projectVersion = parsedToml.getString("project.version");
        
        List<PythonDependency> parsedDirectDependencies = parseDirectDependencies();
        
        return new SetupToolsParsedResult(tomlProjectName, projectVersion, parsedDirectDependencies);
    }
    
    public List<String> load(String setupFile) throws IOException {
        // The pattern "\\[?'(.*)'\\s*\\]?,?|\\[?\"(.*)\"\\s*\\]?,?" works as follows:
        // - "\\[?'(.*)'\\s*\\]?,?" matches dependencies that start with an optional '[' followed by a mandatory single quote,
        //   then any characters (the dependency name), ending with a single quote followed by optional whitespace and an optional ',' or ']'.
        // - The '|' symbol denotes OR, meaning the pattern on either side can match.
        // - "\\[?\"(.*)\"\\s*\\]?,?" is similar to the first part but for dependencies enclosed in double quotes.
        Pattern pattern = Pattern.compile("\\[?'(.*)'\\s*\\]?,?|\\[?\"(.*)\"\\s*\\]?,?");
        
        try (BufferedReader reader = new BufferedReader(new FileReader(setupFile))) {
            String line;
            boolean isInstallRequiresSection = false;

            while ((line = reader.readLine()) != null) {
                // If after removing all whitespace the line starts with install_requires=
                // then we have found the section we are after.
                if (line.trim().replaceAll("\\s+","").startsWith("install_requires=")) {
                    isInstallRequiresSection = true;
                    continue;
                }
                if (isInstallRequiresSection) {
                    // If the [ is on its own line skip it, it doesn't contain a dependency
                    if (line.trim().equals("[")) {
                        continue;
                    }

                    // Using the pattern to match the dependencies in the current line.
                    Matcher matcher = pattern.matcher(line.trim());
                    if (matcher.find()) {
                        // Extracting the dependency from the matched group.
                        String dependency = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
                        // Adding the dependency to the list.
                        dependencies.add(dependency);
                    }
                    
                    // If the line ends with ] or ], it means we have reached the end of the dependencies list.
                    if (line.trim().endsWith("]") || line.trim().endsWith("],")) {
                        break;
                    }
                }
            }
        }

        return dependencies;
    }
    
    private List<PythonDependency> parseDirectDependencies() {
        List<PythonDependency> results = new LinkedList<>();
        
        PythonDependencyTransformer dependencyTransformer = new PythonDependencyTransformer();

        for (String dependencyLine : dependencies) {            
            PythonDependency dependency = dependencyTransformer.transformLine(dependencyLine);
            
            // If we have a ; in our requirements line then there is a condition on this dependency.
            // We want to know this so we don't consider it a failure later if we try to run pip show
            // on it and we don't find it.
            if (dependencyLine.contains(";")) {
                dependency.setConditional(true);
            }

            if (dependency != null) {
                results.add(dependency);
            }
        }
        
        return results;
    }
}
