package com.synopsys.integration.detectable.detectables.clang.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.common.util.parse.CommandParser;
import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommand;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommandParser;

@UnitTest
public class CompileCommandParserTest {
    @Test
    public void testGetCompilerCmd() {
        CompileCommand sampleCommand = new CompileCommand();
        sampleCommand.setCommand("g++ -DDOUBLEQUOTED=\"A value for the compiler\" -DSINGLEQUOTED='Another value for the compiler' file.c -o file.o");

        CompileCommandParser commandParser = new CompileCommandParser(new CommandParser());
        List<String> compilerCommand = commandParser.parseCommand(sampleCommand, Collections.emptyMap());

        assertEquals("g++", compilerCommand.get(0));
    }

    @Test
    public void testGetCompilerArgs() {
        CompileCommand sampleCommand = new CompileCommand();
        sampleCommand.setCommand("g++ -DDOUBLEQUOTED=\"A value for the compiler\" -DSINGLEQUOTED='Another value for the compiler' file.c -o file.o");

        Map<String, String> optionOverrides = new HashMap<>();
        optionOverrides.put("-o", "/dev/null");

        CompileCommandParser commandParser = new CompileCommandParser(new CommandParser());
        List<String> result = commandParser.parseCommand(sampleCommand, optionOverrides);

        for (String part : result) {
            System.out.printf("compiler arg: %s\n", part);
        }

        assertEquals(6, result.size());
        int i = 0;
        assertEquals("g++", result.get(i++));
        assertEquals("-DDOUBLEQUOTED=\"A value for the compiler\"", result.get(i++));
        assertEquals("-DSINGLEQUOTED='Another value for the compiler'", result.get(i++));
        assertEquals("file.c", result.get(i++));
        assertEquals("-o", result.get(i++));
        assertEquals("/dev/null", result.get(i++));
    }

    @Test
    public void testComplexCompileCommand() {
        CompileCommand command = new CompileCommand();
        command.setCommand("/usr/bin/clang++-3.6 -DCMAKE_BUILD_TYPE=\\\"Debug\\\" -DCMAKE_CC_FLAGS=\"\\\" -ggdb -Wstrict-aliasing=2 -pedantic -fPIC --std=c11\\\"\"  -c ./pb.cc");

        CompileCommandParser commandParser = new CompileCommandParser(new CommandParser());
        List<String> result = commandParser.parseCommand(command, Collections.emptyMap());

        assertEquals(5, result.size());
        int i = 0;
        assertEquals("/usr/bin/clang++-3.6", result.get(i++));
        assertEquals("-DCMAKE_BUILD_TYPE=\\\"Debug\\\"", result.get(i++));
        assertEquals("-DCMAKE_CC_FLAGS=\"\\\" -ggdb -Wstrict-aliasing=2 -pedantic -fPIC --std=c11\\\"\"", result.get(i++));
        assertEquals("-c", result.get(i++));
        assertEquals("./pb.cc", result.get(i++));
    }

    @Test
    public void testCrazyNestedQuoting() {
        CompileCommand command = new CompileCommand();
        command.setCommand("X=\"\\\" a  b\\\"\"");

        CompileCommandParser commandParser = new CompileCommandParser(new CommandParser());
        List<String> result = commandParser.parseCommand(command, Collections.emptyMap());

        assertEquals(1, result.size());
        int i = 0;
        assertEquals("X=\"\\\" a  b\\\"\"", result.get(i++));
    }

    @Test
    public void testEscapedDoubleQuotedTerm() {
        CompileCommand command = new CompileCommand();
        command.setDirectory("dir");
        command.setCommand("X=\\\"'a' 'b'\\\"");
        command.setFile("test.cc");

        CompileCommandParser commandParser = new CompileCommandParser(new CommandParser());
        List<String> result = commandParser.parseCommand(command, Collections.emptyMap());
        assertEquals(1, result.size());
        assertEquals("X=\\\"'a' 'b'\\\"", result.get(0));
    }
}
