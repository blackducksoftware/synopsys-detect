package com.synopsys.integration.detectable.detectables.setuptools.parse;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.tomlj.TomlArray;
import org.tomlj.TomlParseResult;

import com.synopsys.integration.detectable.python.util.PythonDependency;
import com.synopsys.integration.detectable.python.util.PythonDependencyTransformer;

public class SetupToolsTomlParser implements SetupToolsParser {
    
    private TomlParseResult parsedToml;
    
    public SetupToolsTomlParser(TomlParseResult parsedToml) {
        this.parsedToml = parsedToml;
    }

    @Override
    public void parse(SetupToolsParsedResult parsedResult) throws IOException {
        List<PythonDependency> parsedDirectDependencies = parseDirectDependencies(parsedToml);
        String projectName = parsedToml.getString("project.name");
        String projectVersion = parsedToml.getString("project.version");

        parsedResult.setProjectName(projectName);
        parsedResult.setProjectVersion(projectVersion);
        parsedResult.getDirectDependencies().addAll(parsedDirectDependencies);
    }

    public List<PythonDependency> parseDirectDependencies(TomlParseResult tomlParseResult) throws IOException {
        List<PythonDependency> results = new LinkedList<>();
        PythonDependencyTransformer dependencyTransformer = new PythonDependencyTransformer();

        TomlArray dependencies = tomlParseResult.getArray("project.dependencies");

        for (int i = 0; i < dependencies.size(); i++) {
            String dependencyLine = dependencies.getString(i);
            
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
