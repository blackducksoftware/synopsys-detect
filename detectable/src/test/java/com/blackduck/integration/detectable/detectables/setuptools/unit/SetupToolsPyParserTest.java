package com.blackduck.integration.detectable.detectables.setuptools.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.blackduck.integration.detectable.detectables.setuptools.parse.SetupToolsParsedResult;
import com.blackduck.integration.detectable.detectables.setuptools.parse.SetupToolsPyParser;
import org.junit.jupiter.api.Test;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import com.blackduck.integration.detectable.python.util.PythonDependency;

public class SetupToolsPyParserTest {
    
    @Test
    public void testLoad() throws IOException {
        String pyContent = "from setuptools import setup\n\nsetup(\ninstall_requires=[\n'requests==2.31.0',\n],\n)";
        Path tempFile = Files.createTempFile("setup", ".py");
        Files.write(tempFile, pyContent.getBytes());

        TomlParseResult result = Toml.parse(pyContent);

        SetupToolsPyParser pyParser = new SetupToolsPyParser(result);
        List<String> dependencies = pyParser.load(tempFile.toString());

        assertEquals(1, dependencies.size());
        assertTrue(dependencies.contains("requests==2.31.0"));

        Files.delete(tempFile); // delete the temporary file
    }
    
    @Test
    public void testParse() throws IOException {
        String pyContent = "from setuptools import setup\n\nsetup(\ninstall_requires=[\n'requests==2.31.0',\n],\n)";
        Path pyFile = Files.createTempFile("setup", ".py");
        Files.write(pyFile, pyContent.getBytes());
        
        String tomlContent = "[build-system]\nrequires = [\"setuptools\"]";

        TomlParseResult result = Toml.parse(tomlContent);

        SetupToolsPyParser pyParser = new SetupToolsPyParser(result);
        pyParser.load(pyFile.toString());
        SetupToolsParsedResult parsedResult = pyParser.parse();

        assertEquals(1, parsedResult.getDirectDependencies().size());
        
        for (PythonDependency dependency : parsedResult.getDirectDependencies()) {
            assertEquals("requests", dependency.getName());
            assertEquals("2.31.0", dependency.getVersion());
        }

        Files.delete(pyFile); // delete the temporary file
    }
}
