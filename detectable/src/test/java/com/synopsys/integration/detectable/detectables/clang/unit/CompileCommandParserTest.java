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

import com.google.gson.Gson;
import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommand;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommandDatabaseParser;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommandParser;

@UnitTest
public class CompileCommandParserTest {
    @Test
    public void testGetCompilerCmd() {
        CompileCommand sampleCommand = new CompileCommand();
        sampleCommand.command = "g++ -DDOUBLEQUOTED=\"A value for the compiler\" -DSINGLEQUOTED='Another value for the compiler' file.c -o file.o";

        CompileCommandParser compileCommandParser = new CompileCommandParser();
        List<String> compilerCommand = compileCommandParser.parseCommand(sampleCommand, Collections.emptyMap());

        assertEquals("g++", compilerCommand.get(0));
    }

    @Test
    public void testGetCompilerArgs() {
        CompileCommand sampleCommand = new CompileCommand();
        sampleCommand.command = "g++ -DDOUBLEQUOTED=\"A value for the compiler\" -DSINGLEQUOTED='Another value for the compiler' file.c -o file.o";

        Map<String, String> optionOverrides = new HashMap<>();
        optionOverrides.put("-o", "/dev/null");

        CompileCommandParser compileCommandParser = new CompileCommandParser();
        List<String> result = compileCommandParser.parseCommand(sampleCommand, optionOverrides);

        for (String part : result) {
            System.out.printf("compiler arg: %s\n", part);
        }

        assertEquals(6, result.size());
        int i = 0;
        assertEquals("g++", result.get(i++));
        assertEquals("-DDOUBLEQUOTED=A value for the compiler", result.get(i++));
        assertEquals("-DSINGLEQUOTED=Another value for the compiler", result.get(i++));
        assertEquals("file.c", result.get(i++));
        assertEquals("-o", result.get(i++));
        assertEquals("/dev/null", result.get(i++));
    }

    @Test
    public void testFullCompileCommand() {
        CompileCommand command = new CompileCommand();
        command.directory = "/home/jslave/sean/mainline/nsulate/src";
        command.command = "/usr/bin/env CCACHE_CPP2=yes /usr/bin/ccache /usr/bin/clang++-3.6  -DAVX2=1 -DCMAKE_BUILD_TYPE=\\\"Debug\\\" -DCMAKE_CC_FLAGS=\"\\\" -ggdb -Werror -Wall -Wstrict-aliasing=2 -pedantic -fPIC -fopenmp --std=c11 -ggdb -Werror -Wall -Wstrict-aliasing=2 -pedantic -fPIC --std=c11\\\"\"  -o CMakeFiles/cli_proto.dir/cli.pb.cc.o -c /home/jslave/sean/mainline/nsulate/src/cli.pb.cc";
        command.file = "/home/jslave/sean/mainline/nsulate/src/cli.pb.cc";

        CompileCommandDatabaseParser compileCommandDatabaseParser = new CompileCommandDatabaseParser(new Gson());
        CompileCommandParser compileCommandParser = new CompileCommandParser();
        List<String> result = compileCommandParser.parseCommand(command, Collections.emptyMap());

        assertEquals(11, result.size());
        int i = 0;
        assertEquals("/usr/bin/env", result.get(i++));
        assertEquals("CCACHE_CPP2=yes", result.get(i++));
        assertEquals("/usr/bin/ccache", result.get(i++));
        assertEquals("/usr/bin/clang++-3.6", result.get(i++));
        assertEquals("-DAVX2=1", result.get(i++));
        assertEquals("-DCMAKE_BUILD_TYPE=\\\"Debug\\\"", result.get(i++));
        assertEquals("-DCMAKE_CC_FLAGS=\" -ggdb -Werror -Wall -Wstrict-aliasing=2 -pedantic -fPIC -fopenmp --std=c11 -ggdb -Werror -Wall -Wstrict-aliasing=2 -pedantic -fPIC --std=c11\"", result.get(i++));
        assertEquals("-o", result.get(i++));
        assertEquals("CMakeFiles/cli_proto.dir/cli.pb.cc.o", result.get(i++));
        assertEquals("-c", result.get(i++));
        assertEquals("/home/jslave/sean/mainline/nsulate/src/cli.pb.cc", result.get(i++));
    }

    @Test
    public void testEscapedDoubleQuotedTerm() {
        CompileCommand command = new CompileCommand();
        command.directory = "dir";
        command.command = "X=\\\"'a' 'b'\\\"";
        command.file = "test.cc";

        CompileCommandDatabaseParser compileCommandDatabaseParser = new CompileCommandDatabaseParser(new Gson());
        CompileCommandParser compileCommandParser = new CompileCommandParser();
        List<String> result = compileCommandParser.parseCommand(command, Collections.emptyMap());
        assertEquals(1, result.size());
        assertEquals("X=\\\"'a' 'b'\\\"", result.get(0));
    }
}
