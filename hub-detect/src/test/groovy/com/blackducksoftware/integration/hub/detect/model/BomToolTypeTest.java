package com.blackducksoftware.integration.hub.detect.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BomToolTypeTest {
    @Test
    public void testFoundName() {
        assertTrue(BomToolType.POSSIBLE_NAMES.contains(BomToolType.CARTHAGE.name()));
    }

    @Test
    public void testNotFoundName() {
        assertFalse(BomToolType.POSSIBLE_NAMES.contains("quoth the raven - NEVERMORE!"));
    }

}
