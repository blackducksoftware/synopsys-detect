package com.blackducksoftware.integration.hub.detect.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class DetectFileFinderTest {

    @Test
    public void testFindContainingDir() {
        final DetectFileFinder finder = new DetectFileFinder();
        assertEquals("clang", finder.findContainingDir(new File("src/test/resources/clang"), 0).getName());
        assertEquals("resources", finder.findContainingDir(new File("src/test/resources/clang"), 1).getName());
        assertEquals("test", finder.findContainingDir(new File("src/test/resources/clang"), 2).getName());
    }

    @Test
    public void testIsFileUnderDir() {
        final DetectFileFinder finder = new DetectFileFinder();
        assertTrue(finder.isFileUnderDir(new File("src/test/resources"), new File("src/test/resources/clang")));
        assertTrue(finder.isFileUnderDir(new File("src/test/resources"), new File("src/test/resources/")));
        assertTrue(finder.isFileUnderDir(new File("src/test/resources"), new File("src/test/resources/clang/../clang")));
        assertFalse(finder.isFileUnderDir(new File("src/test/resources"), new File("src/test/groovy")));
    }

    @Test
    public void testFindFile() {
        final DetectFileFinder finder = new DetectFileFinder();
        assertEquals("findMe.txt", finder.findFile(new File("src/test/resources/bdignore"), "findMe.txt").getName());
    }

    @Test
    public void testBdIgnoreFindFile() {
        final DetectFileFinder finder = new DetectFileFinder();
        assertTrue(finder.isFileUnderDir(new File("src/test/resources/bdignore"), new File("src/test/resources/bdignore/traversedDir/findMe.txt")));
        assertFalse(finder.isFileUnderDir(new File("src/test/resources/bdignore"), new File("src/test/resources/bdignore/ignoredDir1/ignoreMe.txt")));
        assertFalse(finder.isFileUnderDir(new File("src/test/resources/bdignore"), new File("src/test/resources/bdignore/ignoredDir2/ignoreMe.txt")));
    }

    @Test
    public void testBdIgnoreFindFiles() {
        // TODO
    }
}
