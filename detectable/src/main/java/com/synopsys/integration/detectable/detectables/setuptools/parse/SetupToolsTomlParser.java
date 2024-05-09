package com.synopsys.integration.detectable.detectables.setuptools.parse;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
        Map<String, String> parsedDirectDependencies = parseDirectDependencies(parsedToml);
        String projectName = parsedToml.getString("project.name");
        String projectVersion = parsedToml.getString("project.version");
        
        SetupToolsParsedResult result = new SetupToolsParsedResult(projectName, projectVersion, parsedDirectDependencies);
        
        return result;
    }

    public Map<String, String> parseDirectDependencies(TomlParseResult tomlParseResult) throws IOException {
        Map<String, String> results = new HashMap<>();

        TomlArray dependencies = tomlParseResult.getArray("project.dependencies");

        Pattern pattern = Pattern.compile("^([^<>=!\\[ ]+)");
        Pattern versionPattern = Pattern.compile("([0-9]+(\\.[0-9]+)*(-[0-9A-Za-z-.]+)?(\\+[0-9A-Za-z-.]+)?)");

        Comparator<String> versionComparator = (v1, v2) -> {
            String[] parts1 = v1.split("\\.");
            String[] parts2 = v2.split("\\.");
            for (int i = 0; i < Math.min(parts1.length, parts2.length); i++) {
                if (parts1[i].contains("-") || parts1[i].contains("+")) {
                    return -1;
                } else if (parts2[i].contains("-") || parts2[i].contains("+")) {
                    return 1;
                }
                int comparison = Integer.compare(Integer.parseInt(parts1[i]), Integer.parseInt(parts2[i]));
                if (comparison != 0) {
                    return comparison;
                }
            }
            return Integer.compare(parts1.length, parts2.length);
        };

        for (int i = 0; i < dependencies.size(); i++) {
            String dependency = dependencies.getString(i).split(";")[0].split("#")[0];
            Matcher matcher = pattern.matcher(dependency);
            if (matcher.find()) {
                String dependencyName = matcher.group(1);
                String version = "";
                Matcher versionMatcher = versionPattern.matcher(dependency);
                while (versionMatcher.find()) {
                    String currentVersion = versionMatcher.group();
                    if (version.isEmpty() || versionComparator.compare(currentVersion, version) < 0) {
                        version = currentVersion;
                    }
                }
                results.put(dependencyName, version);
            }
        }
        
        return results;
    }
}
