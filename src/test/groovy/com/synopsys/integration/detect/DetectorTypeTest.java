package com.synopsys.integration.detector.detector;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.synopsys.integration.detector.base.DetectorType;

public class DetectorTypeTest {
    @Test
    public void testFoundName() {
        assertTrue(DetectorType.POSSIBLE_NAMES.contains(DetectorType.COCOAPODS.name()));
    }

    @Test
    public void testNotFoundName() {
        assertFalse(DetectorType.POSSIBLE_NAMES.contains("quoth the raven - NEVERMORE!"));
    }

}
