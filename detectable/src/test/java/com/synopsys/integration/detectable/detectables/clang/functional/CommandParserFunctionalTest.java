package com.synopsys.integration.detectable.detectables.clang.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.common.util.parse.CommandParser;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommand;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommandDatabaseParser;
import com.synopsys.integration.detectable.detectables.clang.compilecommand.CompileCommandParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

@FunctionalTest
public class CommandParserFunctionalTest {
    @Test
    public void testCanParseCommandDatabase() throws IOException {
        CompileCommandDatabaseParser compileCommandDatabaseParser = new CompileCommandDatabaseParser(new Gson());

        List<CompileCommand> compileCommands = compileCommandDatabaseParser.parseCompileCommandDatabase(FunctionalTestFiles.asFile("/clang/compile_commands.json"));

        assertEquals(182, compileCommands.size());
        CompileCommand first = compileCommands.get(0);
        assertEquals("/home/jslave/sean/mainline/nsulate/src", first.getDirectory());
        assertTrue(first.getCommand().startsWith("/usr/bin/env CCACHE_CPP2=yes /usr/bin/ccache /usr/bin/clang++-3.6   -DAVX2=1 -DCMAKE_BUILD_TYPE=\\\"Debug\\\""));
        assertEquals("/home/jslave/sean/mainline/nsulate/src/cli.pb.cc", first.getFile());
        assertEquals(0, first.getArguments().length);
    }

    @Test
    public void testCanParseArgumentsFromCommandDatabase() throws IOException {
        CompileCommandDatabaseParser compileCommandDatabaseParser = new CompileCommandDatabaseParser(new Gson());

        List<CompileCommand> compileCommands = compileCommandDatabaseParser.parseCompileCommandDatabase(FunctionalTestFiles.asFile("/clang/compile_commands_args.json"));

        CompileCommand first = compileCommands.get(0);
        CompileCommandParser commandParser = new CompileCommandParser(new CommandParser());

        List<String> result = commandParser.parseCommand(first, Collections.emptyMap());

        assertEquals(66, result.size());
        int i = 0;
        assertEquals("/usr/bin/env", result.get(i++));
        assertEquals("CCACHE_CPP2=yes", result.get(i++));
        assertEquals("/usr/bin/ccache", result.get(i++));
        assertEquals("/usr/bin/clang++-3.6", result.get(i++));
        assertEquals("-DAVX2=1", result.get(i++));
        assertEquals("-DCMAKE_BUILD_TYPE=\\\"Debug\\\"", result.get(i++));
        assertEquals(
            "-DCMAKE_CC_FLAGS=\"\\\" -ggdb -Werror -Wall -Wstrict-aliasing=2 -pedantic -fPIC -fopenmp --std=c11 -ggdb -Werror -Wall -Wstrict-aliasing=2 -pedantic -fPIC --std=c11\\\"\"",
            result.get(i++)
        );
        assertTrue(result.get(i++).startsWith("-DCMAKE_CXX_FLAGS=\"\\\" -ggdb"));
        assertEquals(
            "-DCMAKE_CXX_FLAGS_DEBUG=\"\\\" -ggdb -Werror -Wall -Wstrict-aliasing=2 -pedantic -fPIC -fopenmp -stdlib=libc++ -std=c++14 -DLOG_INTERNAL_ERROR=LOG_DEBUG -mcx16 -msse4.2 -mavx2  \\\"\"",
            result.get(i++)
        );
        assertEquals("-DCMAKE_CXX_FLAGS_RELEASE=\"\\\"-O3 -DNDEBUG -O3 \\\"\"", result.get(i++));
        assertEquals("-DCMAKE_VERSION=\\\"3.5.1\\\"", result.get(i++));
        assertEquals("-DNSULATE_PROJECT_COMMIT=\"\\\"b079181 Create smoke Test suites\\\"\"", result.get(i++));
        assertEquals("-DNSULATE_SYSTEM=\"\\\"Ubuntu 16045 LTS\\\"\"", result.get(i++));
        assertEquals("-DNSULATE_SYSTEM_PROCESSOR=\"\\\"Linux srv-narnia 4.15.0-36-generic x86_64 GNU/Linux\\\"\"", result.get(i++));
        assertEquals("-DNSULATE_TIME_OF_BUILD=\"\\\"Wednesday 14-11-2018 03:22 UTC\\\"\"", result.get(i++));
        assertEquals("-DNSULATE_VERSION=\\\"1.2.82\\\"", result.get(i++));
        assertEquals("-I/home/jslave/sean/mainline/nsulate/include", result.get(i++));
        assertEquals("-I/home/jslave/sean/mainline/nsulate/src", result.get(i++));
    }

    @Test
    public void testComplexNestedQuoting() throws IOException {
        CompileCommandDatabaseParser compileCommandDatabaseParser = new CompileCommandDatabaseParser(new Gson());

        List<CompileCommand> compileCommands = compileCommandDatabaseParser.parseCompileCommandDatabase(FunctionalTestFiles.asFile(
            "/clang/compile_commands_nestedquoting_small.json"));

        CompileCommand first = compileCommands.get(0);
        CompileCommandParser commandParser = new CompileCommandParser(new CommandParser());

        List<String> result = commandParser.parseCommand(first, Collections.emptyMap());

        assertEquals(15, result.size());
        int i = 0;
        assertEquals("cc", result.get(i++));
        assertEquals("-c", result.get(i++));
        assertEquals("-I/usr/include/mit-krb5", result.get(i++));

        String valConfigureAssignment = result.get(i++);
        assertTrue(valConfigureAssignment.startsWith("-DVAL_CONFIGURE"));
        assertTrue(valConfigureAssignment.contains("--with-tclconfig="));

        assertEquals("-DVAL_CC=\\\"gcc\\\"", result.get(i++));
        assertEquals("-DVAL_CPPFLAGS=\\\"-I/usr/include/x86_64-linux-gnu -D_GNU_SOURCE -I/usr/include/libxml2 -I/usr/include/mit-krb5\\\"", result.get(i++));
    }
}
