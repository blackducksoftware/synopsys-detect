package com.synopsys.integration.detectable.detectables.setuptools.parse;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.tomlj.TomlArray;
import org.tomlj.TomlParseResult;

import com.synopsys.integration.detectable.python.util.PythonDependency;
import com.synopsys.integration.detectable.python.util.PythonDependencyTransformer;

public class SetupToolsTomlParser implements SetupToolsParser {
    
    TomlParseResult parsedToml;
    
    public SetupToolsTomlParser(TomlParseResult parsedToml) {
        this.parsedToml = parsedToml;
    }

    @Override
    public SetupToolsParsedResult parse() throws IOException {
        List<PythonDependency> parsedDirectDependencies = parseDirectDependencies(parsedToml);
        String projectName = parsedToml.getString("project.name");
        String projectVersion = parsedToml.getString("project.version");
        
        SetupToolsParsedResult result = new SetupToolsParsedResult(projectName, projectVersion, parsedDirectDependencies);
        
        return result;
    }

    public List<PythonDependency> parseDirectDependencies(TomlParseResult tomlParseResult) throws IOException {
        List<PythonDependency> results = new LinkedList<>();
        PythonDependencyTransformer dependencyTransformer = new PythonDependencyTransformer();

        TomlArray dependencies = tomlParseResult.getArray("project.dependencies");

        for (int i = 0; i < dependencies.size(); i++) {            
            PythonDependency dependency = dependencyTransformer.transformLine(dependencies.getString(i));

            if (dependency != null) {
                results.add(dependency);
            }
        }
        
        return results;
    }
}
