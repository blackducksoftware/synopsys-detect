package com.synopsys.integration.detectable.detectables.setuptools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.tomlj.Toml;
import org.tomlj.TomlArray;
import org.tomlj.TomlParseResult;

import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectables.setuptools.parse.SetupToolsCfgParser;
import com.synopsys.integration.detectable.detectables.setuptools.parse.SetupToolsParser;
import com.synopsys.integration.detectable.detectables.setuptools.parse.SetupToolsTomlParser;

public class SetupToolsExtractUtils {
    
    private static final String BUILD_KEY = "build-system.requires";
    private static final String REQUIRED_KEY = "setuptools";
    private static final String TOML_DEPENDENCIES = "project.dependencies";
    
    private static final String SETUP_CFG = "setup.cfg";
    private static final String SETUP_PY = "setup.py";

    public static TomlParseResult extractToml(File projectToml) throws IOException {
        String projectTomlText = FileUtils.readFileToString(projectToml, StandardCharsets.UTF_8);

        return Toml.parse(projectTomlText);
    }

    public static boolean checkTomlRequiresSetupTools(TomlParseResult parsedToml) {
        if (parsedToml != null) {
            TomlArray buildRequires = parsedToml.getArray(BUILD_KEY);

            if (buildRequires != null) {
                for (int i = 0; i < buildRequires.size(); i++) {
                    String requires = buildRequires.getString(i);

                    if (requires.contains(REQUIRED_KEY)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static SetupToolsParser findDependenciesFile(TomlParseResult parsedToml, Requirements fileResolver) throws IOException {
        // Dependencies, if they exist at all, will be in one of three files.
        // Step 1: Check the pyproject.toml
        TomlArray tomlDependencies = parsedToml.getArray(TOML_DEPENDENCIES);
        
        if (tomlDependencies != null && tomlDependencies.size() > 0) {
            return new SetupToolsTomlParser(parsedToml);
        }
        
        // Step 2: Check the setup.cfg
        File cfgFile = fileResolver.file(SETUP_CFG);
        SetupToolsCfgParser cfgParser = new SetupToolsCfgParser();
        
        List<String> cfgDependencies = cfgParser.load(cfgFile.toString());

        if (cfgDependencies != null && cfgDependencies.size() > 0) {
            return cfgParser;
        }
        
        return null;
    }
}
