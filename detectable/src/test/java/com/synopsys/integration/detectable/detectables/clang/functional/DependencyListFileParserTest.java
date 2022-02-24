package com.synopsys.integration.detectable.detectables.clang.functional;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;

import com.synopsys.integration.detectable.detectables.clang.dependencyfile.DependencyListFileParser;

public class DependencyListFileParserTest {

    @Test
    @ExtendWith(TempDirectory.class)
    public void testSimple(@TempDirectory.TempDir Path tempOutputDirectory) throws IOException {
        Assumptions.assumeFalse(SystemUtils.IS_OS_WINDOWS);

        File baseDir = tempOutputDirectory.toFile();
        File sourceFile = new File(baseDir, "src/test/resources/detectables/functional/clang/src/process.c");
        File includeFile1 = new File(baseDir, "src/test/resources/detectables/functional/clang/include/stdc-predef.h");
        File includeFile2 = new File(baseDir, "src/test/resources/detectables/functional/clang/include/assert.h");
        String fileContents = String.format(
            "dependencies: %s \\\n %s %s\\\n",
            sourceFile.getAbsolutePath(),
            includeFile1.getAbsolutePath(),
            includeFile2.getAbsolutePath()
        );

        DependencyListFileParser parser = new DependencyListFileParser();
        List<String> deps = parser.parseDepsMk(fileContents);

        assertTrue(deps.contains(sourceFile.toPath().normalize().toString()));
        assertTrue(deps.contains(includeFile1.toPath().normalize().toString()));
        assertTrue(deps.contains(includeFile2.toPath().normalize().toString()));
    }

    @Test
    @ExtendWith(TempDirectory.class)
    public void testNonCanonical(@TempDirectory.TempDir Path tempOutputDirectory) throws IOException {
        Assumptions.assumeFalse(SystemUtils.IS_OS_WINDOWS);

        File baseDir = tempOutputDirectory.toFile();
        File sourceFile = new File(baseDir, "src/test/resources/detectables/functional/clang/src/process.c");
        File includeFile1 = new File(baseDir, "src/test/resources/detectables/functional/clang/include/stdc-predef.h");
        File includeFile2 = new File(baseDir, "src/test/resources/../../test/resources/detectables/functional/clang/include/assert.h");
        String fileContents = String.format(
            "dependencies: %s \\\n %s %s\\\n",
            sourceFile.getAbsolutePath(),
            includeFile1.getAbsolutePath(),
            includeFile2.getAbsolutePath()
        );

        DependencyListFileParser parser = new DependencyListFileParser();
        List<String> deps = parser.parseDepsMk(fileContents);

        assertTrue(deps.contains(sourceFile.toPath().normalize().toString()));
        assertTrue(deps.contains(includeFile1.toPath().normalize().toString()));
        assertTrue(deps.contains(includeFile2.toPath().normalize().toString()));
    }
}
