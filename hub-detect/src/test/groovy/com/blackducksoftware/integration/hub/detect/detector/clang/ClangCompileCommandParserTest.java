package com.blackducksoftware.integration.hub.detect.detector.clang;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class ClangCompileCommandParserTest {

    @Test
    public void testGetCompilerCmd() {
        ClangCompileCommandParser compileCommandParser = new ClangCompileCommandParser();

        Map<String, String> optionOverrides = new HashMap<>(1);
        optionOverrides.put("-o", "/dev/null");
        String compilerCommand = compileCommandParser.getCompilerCommand(
            "g++ -DDOUBLEQUOTED=\"A value for the compiler\" -DSINGLEQUOTED='Another value for the compiler' file.c -o file.o");
        assertEquals("g++", compilerCommand);
    }

    @Test
    public void testGetCompilerArgs() {
        ClangCompileCommandParser compileCommandParser = new ClangCompileCommandParser();

        Map<String, String> optionOverrides = new HashMap<>(1);
        optionOverrides.put("-o", "/dev/null");
        List<String> result = compileCommandParser.getCompilerArgsForGeneratingDepsMkFile(
            "g++ -DDOUBLEQUOTED=\"A value for the compiler\" -DSINGLEQUOTED='Another value for the compiler' file.c -o file.o",
            "/testMkFilePath",
            optionOverrides);

        for (String part : result) {
            System.out.printf("compiler arg: %s\n", part);
        }

        assertEquals(8, result.size());
        int i=0;
        assertEquals("-DDOUBLEQUOTED=A value for the compiler", result.get(i++));
        assertEquals("-DSINGLEQUOTED=Another value for the compiler", result.get(i++));
        assertEquals("file.c", result.get(i++));
        assertEquals("-o", result.get(i++));
        assertEquals("/dev/null", result.get(i++));
        assertEquals("-M", result.get(i++));
        assertEquals("-MF", result.get(i++));
        assertEquals("/testMkFilePath", result.get(i++));
    }
}
