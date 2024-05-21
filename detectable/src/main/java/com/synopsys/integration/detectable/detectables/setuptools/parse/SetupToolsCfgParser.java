package com.synopsys.integration.detectable.detectables.setuptools.parse;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.tomlj.TomlParseResult;

import com.synopsys.integration.detectable.python.util.PythonDependency;
import com.synopsys.integration.detectable.python.util.PythonDependencyTransformer;

public class SetupToolsCfgParser implements SetupToolsParser {
    
    private TomlParseResult parsedToml;
    
    private String projectName;
    
    private List<String> dependencies;
    
    public SetupToolsCfgParser(TomlParseResult parsedToml) {
        this.parsedToml = parsedToml;
        this.dependencies = new ArrayList<>();
    }

    @Override
    public void parse(SetupToolsParsedResult parsedResult) throws IOException {
        String tomlProjectName = parsedToml.getString("project.name");
        String projectVersion = parsedToml.getString("project.version");
        
        // If we have multiple project names the name from the toml wins
        // I've only seen version information in the toml so use that.
        String finalProjectName = (tomlProjectName != null && !tomlProjectName.isEmpty()) ? tomlProjectName : projectName;
        
        List<PythonDependency> parsedDirectDependencies = parseDirectDependencies();
        
        parsedResult.setProjectName(finalProjectName);
        parsedResult.setProjectVersion(projectVersion);
        parsedResult.getDirectDependencies().addAll(parsedDirectDependencies);
    }

    /**
     * Extracts, does not parse, any entries in the install_requires section of the
     * setup.cfg
     * 
     * @param filePath path to the setup.cfg file
     * @return a list of dependencies extracted from the install_requires section
     * @throws FileNotFoundException
     * @throws IOException
     */
    public List<String> load(String filePath) throws FileNotFoundException, IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            // This flag is used to indicate whether we are currently reading the lines under the "install_requires" key
            boolean isInstallRequiresSection = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                if (line.startsWith("name")) {
                    parseProjectName(line);
                }

                // Remove all whitespace from the line for key searching
                String keySearch = line.replaceAll("\\s", "");
                
                // If the line starts with "install_requires=", we've found the key we're interested in
                if (keySearch.startsWith("install_requires=")) {
                    isInstallRequiresSection = true;
                    String[] parts = line.split("=", 2);

                    // If there is a value and it's not empty, add it to the dependencies list
                    if (parts.length > 1 && !parts[1].trim().isEmpty()) {
                        dependencies.add(parts[1].trim());
                    }
                }
                else if (isInstallRequiresSection) {
                    if (isEndofInstallRequiresSection(line)) { 
                        break;
                    }
                    // If the line is not empty, add it to the dependencies list
                    else if (!line.isEmpty()) {
                        dependencies.add(line);
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

    public void parseProjectName(String line) {
        String[] parts = line.split("=", 2);
        if (parts.length > 1 && !parts[1].trim().isEmpty()) {
            projectName = parts[1].trim();
        }
    }

    private boolean isEndofInstallRequiresSection(String line) {
        /*
         * If the line starts with a [ we have reached a new section and want to exit.
         * 
         * The line.matches call looks for a new key. 
         * It will return true if the string starts with optional whitespace, 
         * followed by one or more alphanumeric characters, periods, underscores, or hyphens, 
         * (which is the allowed set of characters for a key), followed by
         * optional whitespace, an equal sign, optional whitespace, and then any
         * character that is not another =, !, <, >, or ~ which would indicate a requirement 
         * operator and not a new key.
         */
        if (line.startsWith("[") || line.matches("^\\s*[a-zA-Z0-9_.-]+\\s*=\\s*(?![=!<>~]).*$")) {
            return true;
        } else {
            return false;
        }
    }
}
