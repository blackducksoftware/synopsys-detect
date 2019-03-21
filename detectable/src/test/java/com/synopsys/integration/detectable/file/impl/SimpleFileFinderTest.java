package com.synopsys.integration.detectable.file.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.filefilter.CanReadFileFilter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectable.file.impl.SimpleFileFinder;


public class SimpleFileFinderTest {

    private static Path initialDirectoryPath;

    @BeforeAll
    public static void setup() throws IOException {
        initialDirectoryPath = Files.createTempDirectory("DetectorFinderTest");
    }

    @AfterAll
    public static void cleanup() {
        initialDirectoryPath.toFile().delete();
    }

    @Test
    public void testSymlinksNotFollowed() throws IOException {
        // Create a subDir with a symlink that loops back to its parent
        final File initialDirectory = initialDirectoryPath.toFile();
        final File subDir = new File(initialDirectory, "sub");
        subDir.mkdirs();
        final File link = new File(subDir, "linkToInitial");
        final Path linkPath = link.toPath();
        Files.createSymbolicLink(linkPath, initialDirectoryPath);

        final File regularDir = new File(subDir, "regularDir");
        regularDir.mkdir();
        final File regularFile = new File(subDir, "regularFile");
        regularFile.createNewFile();

        final SimpleFileFinder finder = new SimpleFileFinder();
        final List<String> filenamePatterns = Arrays.asList("sub", "linkToInitial", "regularDir", "regularFile");
        final List<File> foundFiles = finder.findFiles(initialDirectoryPath.toFile(), filenamePatterns, 10);

        // make sure symlink not followed during dir traversal
        assertEquals(4, foundFiles.size());
    }


}
