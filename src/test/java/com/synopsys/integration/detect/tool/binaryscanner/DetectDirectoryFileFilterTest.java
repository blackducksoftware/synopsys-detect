package com.synopsys.integration.detect.tool.binaryscanner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.util.finder.DetectDirectoryFileFilter;

public class DetectDirectoryFileFilterTest {
    private static File tempDir;
    private static File includedFile;
    private static File includedDir;
    private static File includedFileInIncludedDir;
    private static File nonMatchingFile;
    private static File excludedDir;

    @BeforeAll
    static void setup() throws IOException {
        tempDir = Files.createTempDirectory("junit_test_filtertest").toFile();

        includedFile = new File(tempDir, "includeme.txt");
        includedFile.createNewFile();
        includedDir = new File(tempDir, "dir");
        includedDir.mkdir();
        includedFileInIncludedDir = new File(includedDir, "includeme2.txt");
        includedFileInIncludedDir.createNewFile();
        nonMatchingFile = new File(includedDir, "nonmatching.jar");
        nonMatchingFile.createNewFile();
        excludedDir = new File(includedDir, "excludeme");
        excludedDir.mkdir();

    }

    @AfterAll
    static void cleanup() throws IOException {
        if (tempDir.isDirectory()) {
            FileUtils.deleteDirectory(tempDir);
        }
    }

    @Test
    void testFileInclusion() {

        List<String> excludedDirs = Arrays.asList("excludeme");
        List<String> includedFiles = Arrays.asList("*.txt");
        DetectDirectoryFileFilter filter = new DetectDirectoryFileFilter(excludedDirs, includedFiles);

        assertTrue(filter.test(includedFile));
        assertTrue(filter.test(includedFileInIncludedDir));

        assertFalse(filter.test(new File(tempDir, "dir/nonexistent.txt")));
        assertFalse(filter.test(nonMatchingFile));
    }

    @Test
    void testDirExclusion() {
        List<String> excludedDirs = Arrays.asList("excludeme");
        List<String> includedFiles = Arrays.asList("*.txt");
        DetectDirectoryFileFilter filter = new DetectDirectoryFileFilter(excludedDirs, includedFiles);

        assertFalse(filter.test(includedDir));
        assertTrue(filter.test(excludedDir));
    }
}
