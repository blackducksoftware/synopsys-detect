/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detectable.detectables.clang.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommand;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.ArgumentParser;

@UnitTest
public class ArgumentParserTest {
    @Test
    public void testGetCompilerCmd() {
        CompileCommand sampleCommand = new CompileCommand();
        sampleCommand.command = "g++ -DDOUBLEQUOTED=\"A value for the compiler\" -DSINGLEQUOTED='Another value for the compiler' file.c -o file.o";

        ArgumentParser argumentParser = new ArgumentParser();
        List<String> compilerCommand = argumentParser.parseCommand(sampleCommand, Collections.emptyMap());

        assertEquals("g++", compilerCommand.get(0));
    }

    @Test
    public void testGetCompilerArgs() {
        CompileCommand sampleCommand = new CompileCommand();
        sampleCommand.command = "g++ -DDOUBLEQUOTED=\"A value for the compiler\" -DSINGLEQUOTED='Another value for the compiler' file.c -o file.o";

        Map<String, String> optionOverrides = new HashMap<>();
        optionOverrides.put("-o", "/dev/null");

        ArgumentParser argumentParser = new ArgumentParser();
        List<String> result = argumentParser.parseCommand(sampleCommand, optionOverrides);

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
        command.command = "/usr/bin/clang++-3.6 -DCMAKE_BUILD_TYPE=\\\"Debug\\\" -DCMAKE_CC_FLAGS=\"\\\" -ggdb -Wstrict-aliasing=2 -pedantic -fPIC --std=c11\\\"\"  -c ./pb.cc";

        ArgumentParser argumentParser = new ArgumentParser();
        List<String> result = argumentParser.parseCommand(command, Collections.emptyMap());

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
        command.command = "X=\"\\\" a  b\\\"\"";

        ArgumentParser argumentParser = new ArgumentParser();
        List<String> result = argumentParser.parseCommand(command, Collections.emptyMap());

        assertEquals(1, result.size());
        int i = 0;
        assertEquals("X=\"\\\" a  b\\\"\"", result.get(i++));
    }

    @Test
    public void testEscapedDoubleQuotedTerm() {
        CompileCommand command = new CompileCommand();
        command.directory = "dir";
        command.command = "X=\\\"'a' 'b'\\\"";
        command.file = "test.cc";

        ArgumentParser argumentParser = new ArgumentParser();
        List<String> result = argumentParser.parseCommand(command, Collections.emptyMap());
        assertEquals(1, result.size());
        assertEquals("X=\\\"'a' 'b'\\\"", result.get(0));
    }
}
