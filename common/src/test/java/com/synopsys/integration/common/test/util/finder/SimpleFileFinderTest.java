package com.synopsys.integration.common.test.util.finder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.condition.OS.WINDOWS;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;

import com.synopsys.integration.common.util.finder.SimpleFileFinder;

public class SimpleFileFinderTest {

    private static Path initialDirectoryPath;

    @BeforeEach
    public void setup() throws IOException {
        initialDirectoryPath = Files.createTempDirectory("SimpleFileFinderTest");
    }

    @AfterEach
    public void cleanup() throws IOException {
        try {
            Files.delete(initialDirectoryPath);
        } catch (DirectoryNotEmptyException e) {            
            try (Stream<Path> walk = Files.walk(initialDirectoryPath)) {
                walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            }
        }
    }

    @Test
    @DisabledOnOs(WINDOWS)
    public void testSymlinksNotFollowed() throws IOException {
        List<File> files = findFiles(false);
        // make sure symlink not followed during dir traversal
        assertEquals(4, files.size());
    }

    @Test
    @DisabledOnOs(WINDOWS)
    public void testSymLinksAreFollowed() throws IOException {
        List<File> files = findFiles(true);
        // make sure symlink followed during dir traversal, enters cyclical link and finds duplicate files
        assertTrue(files.size() > 4);
    }

    private List<File> findFiles(boolean followSymLinks) throws IOException {
        // Create a subDir with a symlink that points to isolated directory
        File initialDirectory = initialDirectoryPath.toFile();
        File subDir = new File(initialDirectory, "sub");
        subDir.mkdirs();
        File link = new File(subDir, "linkToInitial");
        Path linkPath = link.toPath();
        Files.createSymbolicLink(linkPath, initialDirectoryPath);

        File regularDir = new File(subDir, "regularDir");
        regularDir.mkdir();
        File regularFile = new File(subDir, "regularFile");
        regularFile.createNewFile();

        SimpleFileFinder finder = new SimpleFileFinder();
        List<String> filenamePatterns = Arrays.asList("sub", "linkToInitial", "regularDir", "regularFile");
        return finder.findFiles(initialDirectoryPath.toFile(), filenamePatterns, followSymLinks, 10);
    }

    @Test
    public void testFindWithPredicate() throws IOException {
        File initialDirectory = initialDirectoryPath.toFile();

        File subDir1 = new File(initialDirectory, "sub1");
        subDir1.mkdirs();
        File subDirChild1 = new File(subDir1, "child");
        subDirChild1.createNewFile();

        File subDir2 = new File(initialDirectory, "sub2");
        subDir2.mkdirs();
        File subDirChild2 = new File(subDir2, "child");
        subDirChild2.createNewFile();

        SimpleFileFinder fileFinder = new SimpleFileFinder();
        Predicate<File> filter = file -> file.getName().startsWith("sub");
        List<File> foundFiles = fileFinder.findFiles(initialDirectoryPath.toFile(), filter, false, 10);

        assertEquals(2, foundFiles.size());
        assertFalse(foundFiles.contains(subDirChild2));
    }
}
