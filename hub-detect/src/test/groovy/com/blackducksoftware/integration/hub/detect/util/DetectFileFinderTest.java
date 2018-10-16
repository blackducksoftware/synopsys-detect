package com.blackducksoftware.integration.hub.detect.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;

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

}
