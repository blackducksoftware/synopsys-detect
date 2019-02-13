package com.synopsys.integration.detect.detector.clang;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.gson.Gson;

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

    @Test
    public void testGetCompilerArgsFromJsonFile() throws IOException {
        final List<CompileCommand> compileCommands = CompileCommandsJsonFile.parseJsonCompilationDatabaseFile(new Gson(), new File("src/test/resources/clang/compile_commands.json"));
        ClangCompileCommandParser compileCommandParser = new ClangCompileCommandParser();

        Map<String, String> optionOverrides = new HashMap<>(1);
        optionOverrides.put("-o", "/dev/null");
        List<String> result = compileCommandParser.getCompilerArgsForGeneratingDepsMkFile(
            compileCommands.get(0).getCommand(),
            "/testMkFilePath",
            optionOverrides);

        for (String part : result) {
            System.out.printf("compiler arg: %s\n", part);
        }

        verifyResults(result);
    }

    @Test
    public void testGetCompilerArgsFromJsonFileUsingArgs() throws IOException {
        final List<CompileCommand> compileCommands = CompileCommandsJsonFile.parseJsonCompilationDatabaseFile(new Gson(), new File("src/test/resources/clang/compile_commands_args.json"));
        ClangCompileCommandParser compileCommandParser = new ClangCompileCommandParser();

        Map<String, String> optionOverrides = new HashMap<>(1);
        optionOverrides.put("-o", "/dev/null");
        List<String> result = compileCommandParser.getCompilerArgsForGeneratingDepsMkFile(
            compileCommands.get(0).getCommand(),
            "/testMkFilePath",
            optionOverrides);

        for (String part : result) {
            System.out.printf("compiler arg: %s\n", part);
        }

        verifyResults(result);
    }

    private void verifyResults(final List<String> result) {
        assertEquals(68, result.size());
        int i=0;
        assertEquals("CCACHE_CPP2=yes", result.get(i++));
        assertEquals("/usr/bin/ccache", result.get(i++));
        assertEquals("/usr/bin/clang++-3.6", result.get(i++));
        assertEquals("-DAVX2=1", result.get(i++));
        assertEquals("-DCMAKE_BUILD_TYPE=\"Debug\"", result.get(i++));
        assertEquals("-DCMAKE_CC_FLAGS=\" -ggdb -Werror -Wall -Wstrict-aliasing=2 -pedantic -fPIC -fopenmp --std=c11 -ggdb -Werror -Wall -Wstrict-aliasing=2 -pedantic -fPIC --std=c11\"", result.get(i++));
        assertEquals("-DCMAKE_CXX_FLAGS=\" -ggdb -Werror -Wall -Wstrict-aliasing=2 -pedantic -fPIC -fopenmp -stdlib=libc++ -std=c++14 -DLOG_INTERNAL_ERROR=LOG_DEBUG -mcx16 -msse4.2 -mavx2  -ggdb -Werror -Wall -Wstrict-aliasing=2 -pedantic -fPIC -stdlib=libc++ -std=c++14 -DLOG_INTERNAL_ERROR=LOG_DEBUG\"", result.get(i++));
        assertEquals("-DCMAKE_CXX_FLAGS_DEBUG=\" -ggdb -Werror -Wall -Wstrict-aliasing=2 -pedantic -fPIC -fopenmp -stdlib=libc++ -std=c++14 -DLOG_INTERNAL_ERROR=LOG_DEBUG -mcx16 -msse4.2 -mavx2  \"", result.get(i++));
        assertEquals("-DCMAKE_CXX_FLAGS_RELEASE=\"-O3 -DNDEBUG -O3 \"", result.get(i++));
        assertEquals("-DCMAKE_VERSION=\"3.5.1\"", result.get(i++));
        assertEquals("-DNSULATE_PROJECT_COMMIT=\"b079181 Create smoke Test suites\"", result.get(i++));
        assertEquals("-DNSULATE_SYSTEM=\"Ubuntu 16045 LTS\"", result.get(i++));
        assertEquals("-DNSULATE_SYSTEM_PROCESSOR=\"Linux srv-narnia 4.15.0-36-generic x86_64 GNU/Linux\"", result.get(i++));
        assertEquals("-DNSULATE_TIME_OF_BUILD=\"Wednesday 14-11-2018 03:22 UTC\"", result.get(i++));
        assertEquals("-DNSULATE_VERSION=\"1.2.82\"", result.get(i++));
        assertEquals("-I/home/jslave/sean/mainline/nsulate/include", result.get(i++));
        assertEquals("-I/home/jslave/sean/mainline/nsulate/src", result.get(i++));
    }
}
