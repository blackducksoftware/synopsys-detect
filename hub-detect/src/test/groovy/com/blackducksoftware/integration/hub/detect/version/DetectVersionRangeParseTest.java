package com.blackducksoftware.integration.hub.detect.version;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

public class DetectVersionRangeParseTest {
    @Test
    public void testExact() {
        DetectVersionRange range = DetectVersionRange.fromString("4.56.2");
        assertFalse(range.isMajorWildcard());
        assertFalse(range.isMinorWildcard());
        assertFalse(range.isPatchWildcard());
        assertEquals(4, range.getMajorVersion());
        assertEquals(56, range.getMinorVersion());
        assertEquals(2, range.getPatchVersion());
    }

    @Test
    public void testPatchWildcard() {
        DetectVersionRange range = DetectVersionRange.fromString("1.2.*");
        assertFalse(range.isMajorWildcard());
        assertFalse(range.isMinorWildcard());
        assertTrue(range.isPatchWildcard());
        assertEquals(1, range.getMajorVersion());
        assertEquals(2, range.getMinorVersion());
        assertEquals(0, range.getPatchVersion());
    }

    @Test
    public void testMinorPatchWildcard() {
        DetectVersionRange range = DetectVersionRange.fromString("1.*");
        assertFalse(range.isMajorWildcard());
        assertTrue(range.isMinorWildcard());
        assertTrue(range.isPatchWildcard());
        assertEquals(1, range.getMajorVersion());
        assertEquals(0, range.getMinorVersion());
        assertEquals(0, range.getPatchVersion());
    }

    @Test
    public void testMajorMinorPatchWildcard() {
        DetectVersionRange range = DetectVersionRange.fromString("*");
        assertTrue(range.isMajorWildcard());
        assertTrue(range.isMinorWildcard());
        assertTrue(range.isPatchWildcard());
        assertEquals(0, range.getMajorVersion());
        assertEquals(0, range.getMinorVersion());
        assertEquals(0, range.getPatchVersion());
    }
}
