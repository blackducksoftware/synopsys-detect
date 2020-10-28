package com.synopsys.integration.detectable.detectables.clang.functional;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.clang.dependencyfile.DependenyListFileParser;

public class DependencyListFileParserTest {

    @Test
    public void testSimple() {
        final String curDirPath = System.getProperty("user.dir");
        final File curDir = new File(curDirPath);
        final File sourceFile = new File(curDir, "src/test/resources/detectables/functional/clang/src/process.c");
        final File includeFile1 = new File(curDir, "src/test/resources/detectables/functional/clang/include/stdc-predef.h");
        final File includeFile2 = new File(curDir, "src/test/resources/detectables/functional/clang/include/assert.h");
        final String fileContents = String.format("dependencies: %s \\\n %s %s\\\n",
            sourceFile.getAbsolutePath(), includeFile1.getAbsolutePath(), includeFile2.getAbsolutePath());

        final DependenyListFileParser parser = new DependenyListFileParser();
        final List<String> deps = parser.parseDepsMk(fileContents);

        for (final String dep : deps) {
            System.out.printf("dep: %s\n", dep);
        }
        assertTrue(deps.contains(sourceFile.getAbsolutePath()));
        assertTrue(deps.contains(includeFile1.getAbsolutePath()));
        assertTrue(deps.contains(includeFile2.getAbsolutePath()));
    }

    @Test
    public void testNonCanonical() throws IOException {
        final String curDirPath = System.getProperty("user.dir");
        final File curDir = new File(curDirPath);
        final File sourceFile = new File(curDir, "src/test/resources/detectables/functional/clang/src/process.c");
        final File includeFile1 = new File(curDir, "src/test/resources/detectables/functional/clang/include/stdc-predef.h");
        final File includeFile2 = new File(curDir, "src/test/resources/../../test/resources/detectables/functional/clang/include/assert.h");
        final String fileContents = String.format("dependencies: %s \\\n %s %s\\\n",
            sourceFile.getAbsolutePath(), includeFile1.getAbsolutePath(), includeFile2.getAbsolutePath());

        final DependenyListFileParser parser = new DependenyListFileParser();
        final List<String> deps = parser.parseDepsMk(fileContents);

        for (final String dep : deps) {
            System.out.printf("dep: %s\n", dep);
        }
        assertTrue(deps.contains(sourceFile.getCanonicalPath()));
        assertTrue(deps.contains(includeFile1.getCanonicalPath()));
        assertTrue(deps.contains(includeFile2.getCanonicalPath()));
    }
}
