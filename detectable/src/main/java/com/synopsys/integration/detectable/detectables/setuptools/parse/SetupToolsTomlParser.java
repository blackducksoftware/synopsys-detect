package com.synopsys.integration.detectable.detectables.setuptools.parse;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tomlj.TomlArray;
import org.tomlj.TomlParseResult;

public class SetupToolsTomlParser implements SetupToolsParser {
    
    TomlParseResult parsedToml;
    
    public SetupToolsTomlParser(TomlParseResult parsedToml) {
        this.parsedToml = parsedToml;
    }

    @Override
    public SetupToolsParsedResult parse() throws IOException {
        Set<String> parsedDirectDependencies = parseDirectDependencies(parsedToml);
        String projectName = parsedToml.getString("project.name");
        String projectVersion = parsedToml.getString("project.version");
        
        SetupToolsParsedResult result = new SetupToolsParsedResult(projectName, projectVersion, parsedDirectDependencies);
        
        return result;
    }
    
    // TODO this might only work for the build detector as I'm not currently trying to extract
    // the version.
    public Set<String> parseDirectDependencies(TomlParseResult tomlParseResult) throws IOException {
        Set<String> results = new HashSet<>();
        
        TomlArray dependencies = tomlParseResult.getArray("project.dependencies");
        
        Pattern pattern = Pattern.compile("^[^<>=! ]+");

        for (int i = 0; i < dependencies.size(); i++) {
            Matcher matcher = pattern.matcher(dependencies.getString(i));
            if (matcher.find()) {
                results.add(matcher.group());
            }
        }
        
        return results;
    }
}
