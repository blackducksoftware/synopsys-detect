package com.blackducksoftware.integration.hub.detect.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;

public class BomToolTypeTest {
    @Test
    public void testFoundName() {
        assertTrue(BomToolGroupType.POSSIBLE_NAMES.contains(BomToolGroupType.CARTHAGE.name()));
    }

    @Test
    public void testNotFoundName() {
        assertFalse(BomToolGroupType.POSSIBLE_NAMES.contains("quoth the raven - NEVERMORE!"));
    }

}
