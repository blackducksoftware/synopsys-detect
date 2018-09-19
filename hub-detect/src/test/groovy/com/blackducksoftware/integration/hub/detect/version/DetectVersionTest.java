package com.blackducksoftware.integration.hub.detect.version;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DetectVersionTest {
    @Test
    public void testExact() {
        DetectVersion version = DetectVersion.fromString("4.56.2");
        assertEquals(4, version.getMajorVersion());
        assertEquals(56, version.getMinorVersion());
        assertEquals(2, version.getPatchVersion());
    }
}
