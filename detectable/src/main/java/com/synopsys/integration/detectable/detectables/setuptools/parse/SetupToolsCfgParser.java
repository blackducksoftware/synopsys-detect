package com.synopsys.integration.detectable.detectables.setuptools.parse;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SetupToolsCfgParser implements SetupToolsParser {

    @Override
    public SetupToolsParsedResult parse() throws IOException {
        // TODO Auto-generated method stub
        return null;
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
        List<String> dependencies = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            // This flag is used to indicate whether we are currently reading the lines under the "install_requires" key
            boolean isInstallRequiresSection = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

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
                    // If the line starts with "[" or matches the pattern for a new key, we've reached a new section or a new key.
                    // So we stop adding to dependencies. 
                    
                    // A new key is defined by alphanumeric characters, followed by optional
                    // whitespace, followed by a single =, followed by optional whitespace, followed by more alphanumeric characters
                    // TODO this needs to parse complex things
                    // package names must match
                    if (line.startsWith("[") 
                            || line.matches("^\\s*[a-zA-Z0-9_.-]+\\s*=\\s*(?![=!<>~]).*$")) {
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
}
