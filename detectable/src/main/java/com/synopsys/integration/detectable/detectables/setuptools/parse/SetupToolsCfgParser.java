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
