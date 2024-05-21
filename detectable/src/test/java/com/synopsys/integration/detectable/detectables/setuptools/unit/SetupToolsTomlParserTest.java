package com.synopsys.integration.detectable.detectables.setuptools.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import com.synopsys.integration.detectable.detectables.setuptools.parse.SetupToolsParsedResult;
import com.synopsys.integration.detectable.detectables.setuptools.parse.SetupToolsTomlParser;

public class SetupToolsTomlParserTest {
    
    @Test
    public void testParse() throws IOException {
        String tomlContent = "[project]\nname = \"setuptools\"\nversion = \"1.0.0\"\ndependencies = [\n    \"requests\",\n    \"numpy\"\n]";
        Path pyProjectFile = Files.createTempFile("pyproject", ".toml");
        Files.write(pyProjectFile, tomlContent.getBytes());

        TomlParseResult result = Toml.parse(tomlContent);

        SetupToolsTomlParser tomlParser = new SetupToolsTomlParser(result);
        SetupToolsParsedResult parsedResult = tomlParser.parse();

        assertEquals("setuptools", parsedResult.getProjectName());
        assertEquals("1.0.0", parsedResult.getProjectVersion());
        assertEquals(2, parsedResult.getDirectDependencies().size());

        Files.delete(pyProjectFile); // delete the temporary file
    }
}
