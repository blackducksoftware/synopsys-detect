package com.blackduck.integration.detectable.detectables.setuptools.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.blackduck.integration.detectable.detectables.setuptools.parse.SetupToolsCfgParser;
import com.blackduck.integration.detectable.detectables.setuptools.parse.SetupToolsParsedResult;
import org.junit.jupiter.api.Test;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import com.blackduck.integration.detectable.python.util.PythonDependency;

public class SetupToolsCfgParserTest {
    
    @Test
    public void testLoad() throws IOException {
        String cfgContent = "[metadata]\nname = \"setuptools\"\n\n[options]\ninstall_requires =\n    requests\n    numpy";
        Path tempFile = Files.createTempFile("setup", ".cfg");
        Files.write(tempFile, cfgContent.getBytes());

        TomlParseResult result = Toml.parse(cfgContent);

        SetupToolsCfgParser cfgParser = new SetupToolsCfgParser(result);
        List<String> dependencies = cfgParser.load(tempFile.toString());

        assertEquals(2, dependencies.size());
        assertTrue(dependencies.contains("requests"));
        assertTrue(dependencies.contains("numpy"));

        Files.delete(tempFile); // delete the temporary file
    }
    
    @Test
    public void testParse() throws IOException {
        String cfgContent = "[metadata]\nname = setuptools\n[options]\ninstall_requires =\n    requests\n    numpy";
        Path cfgFile = Files.createTempFile("setup", ".cfg");
        Files.write(cfgFile, cfgContent.getBytes());
        
        String tomlContent = "[build-system]\nrequires = [\"setuptools\"]";

        TomlParseResult result = Toml.parse(tomlContent);

        SetupToolsCfgParser cfgParser = new SetupToolsCfgParser(result);
        cfgParser.load(cfgFile.toString());
        SetupToolsParsedResult parsedResult = cfgParser.parse();

        assertEquals("setuptools", parsedResult.getProjectName());
        assertEquals(2, parsedResult.getDirectDependencies().size());
        
        for (PythonDependency dependency : parsedResult.getDirectDependencies()) {
            if (!dependency.getName().equals("requests") && !dependency.getName().equals("numpy")) {
                fail();
            }
        }

        Files.delete(cfgFile); // delete the temporary file
    }
}
