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

        TomlArray dependencies = tomlParseResult.getArray("project.dependencies");
        
        PythonDependencyTransformer dependencyTransformer = new PythonDependencyTransformer();

        // Compile a regular expression pattern to match the dependency name
        //Pattern pattern = Pattern.compile("^([^<>=!\\[ ]+)");

        /*
         * ([0-9]+(\\.[0-9]+)*): This part matches the MAJOR.MINOR.PATCH structure of a version string. 
         * It matches one or more digits ([0-9]+), followed by zero or more occurrences of a dot and one or more digits ((\\.[0-9]+)*).
         * 
         * (-[0-9A-Za-z-.]+)?: This part matches the pre-release version part of a version string. 
         * It matches a hyphen followed by one or more alphanumeric characters, hyphens, or dots (-[0-9A-Za-z-.]+). 
         * The question mark at the end makes this part optional (?), as a version string may not include a pre-release version.
         * 
         * (\\+[0-9A-Za-z-.]+)?: This part matches the build metadata part of a version string. It matches a plus sign 
         * followed by one or more alphanumeric characters, hyphens, or dots (\\+[0-9A-Za-z-.]+). 
         * The question mark at the end makes this part optional (?), as a version string may not include build metadata.
         * 
         * This regular expression is designed to match version strings according to the Semantic Versioning (SemVer) specification.
         */
        //Pattern versionPattern = Pattern.compile("([0-9]+(\\.[0-9]+)*(-[0-9A-Za-z-.]+)?(\\+[0-9A-Za-z-.]+)?)");

        //Comparator<String> versionComparator = createVersionComparator();

        for (int i = 0; i < dependencies.size(); i++) {
            // Split the dependency string by the semicolon and the hash, and only keep the part before them
//            String dependency = dependencies.getString(i).split(";")[0].split("#")[0];
//
//            Matcher matcher = pattern.matcher(dependency);
//            if (matcher.find()) {
//                String dependencyName = matcher.group(1);
//
//                String version = "";
//                Matcher versionMatcher = versionPattern.matcher(dependency);
//                
//                while (versionMatcher.find()) {
//                    String currentVersion = versionMatcher.group();
//
//                    // If the version is empty or the current version is smaller than the version, update the version
//                    if (version.isEmpty() || versionComparator.compare(currentVersion, version) < 0) {
//                        version = currentVersion;
//                    }
//                }
//
//                results.put(dependencyName, version);
//            }
            
            PythonDependency dependency = dependencyTransformer.transformLine(dependencies.getString(i));

            if (dependency != null) {
                results.add(dependency);
            }
        }
        
        return results;
    }
    
    // Define a comparator to compare versions according to Semantic Versioning (SemVer)
//    private Comparator<String> createVersionComparator() {
//        return (v1, v2) -> {
//            // Split the versions into parts
//            String[] parts1 = v1.split("\\.");
//            String[] parts2 = v2.split("\\.");
//
//            // Iterate over the parts of the versions
//            for (int i = 0; i < Math.min(parts1.length, parts2.length); i++) {
//                // If the first version part contains a '-' or '+', it is a pre-release version
//                // or has build metadata, so it has lower precedence
//                if (parts1[i].contains("-") || parts1[i].contains("+")) {
//                    return -1;
//                }
//                // If the second version part contains a '-' or '+', it is a pre-release version
//                // or has build metadata, so it has lower precedence
//                else if (parts2[i].contains("-") || parts2[i].contains("+")) {
//                    return 1;
//                }
//
//                // Compare the integer values of the version parts
//                int comparison = Integer.compare(Integer.parseInt(parts1[i]), Integer.parseInt(parts2[i]));
//
//                // If the parts are not equal, return the comparison result
//                if (comparison != 0) {
//                    return comparison;
//                }
//            }
//
//            // If all compared parts are equal, compare the lengths of the versions
//            // A version with more parts has higher precedence
//            return Integer.compare(parts1.length, parts2.length);
//        };
//    }
}
