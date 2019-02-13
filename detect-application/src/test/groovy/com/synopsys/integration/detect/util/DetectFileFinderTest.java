package com.synopsys.integration.detect.util;

import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.synopsys.integration.detect.workflow.file.DetectFileFinder;

public class DetectFileFinderTest {

    @Test
    public void testFindContainingDir() {
        final DetectFileFinder finder = new DetectFileFinder();
        File targetDir = new File("src/test/resources/clang");
        assertEquals("clang", finder.findContainingDir(targetDir, 0).getName());
        assertEquals("resources", finder.findContainingDir(targetDir, 1).getName());
        assertEquals("test", finder.findContainingDir(targetDir, 2).getName());
    }

    @Test
    public void testIsFileUnderDir() {
        final DetectFileFinder finder = new DetectFileFinder();
        File targetDir = new File("src/test/resources");
        assertTrue(finder.isFileUnderDir(targetDir, new File("src/test/resources/clang")));
        assertTrue(finder.isFileUnderDir(targetDir, new File("src/test/resources/")));
        assertTrue(finder.isFileUnderDir(targetDir, new File("src/test/resources/clang/../clang")));
        assertFalse(finder.isFileUnderDir(targetDir, new File("src/test/groovy")));
    }

    @Test
    public void testFindAllFilesToMaxDepth() {
        final DetectFileFinder finder = new DetectFileFinder();
        File targetDir = new File("src/test/resources/fileFinder");
        List<File> filesFound = finder.findAllFilesToMaxDepth(targetDir, "*.txt");
        assertEquals(4, filesFound.size());
    }

    @Test
    public void testFindAllFilesToDepth() {
        final DetectFileFinder finder = new DetectFileFinder();
        File targetDir = new File("src/test/resources/fileFinder");
        List<File> filesFound = finder.findAllFilesToDepth(targetDir, new StringBuilder("Maximum search depth hit during test at %s"), 2,"*.txt");
        assertEquals(1, filesFound.size());
    }
    @Test
    public void testFindAllFilesToDepthSimpleMsgString() {
        final DetectFileFinder finder = new DetectFileFinder();
        File targetDir = new File("src/test/resources/fileFinder");
        List<File> filesFound = finder.findAllFilesToDepth(targetDir, new StringBuilder("Maximum search depth hit during test"), 2,"*.txt");
        assertEquals(1, filesFound.size());
    }
}
