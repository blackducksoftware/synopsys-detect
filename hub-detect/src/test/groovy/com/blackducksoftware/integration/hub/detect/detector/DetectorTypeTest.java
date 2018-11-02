package com.blackducksoftware.integration.hub.detect.detector;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DetectorTypeTest {
    @Test
    public void testFoundName() {
        assertTrue(DetectorType.POSSIBLE_NAMES.contains(DetectorType.CARTHAGE.name()));
    }

    @Test
    public void testNotFoundName() {
        assertFalse(DetectorType.POSSIBLE_NAMES.contains("quoth the raven - NEVERMORE!"));
    }

}
