package com.synopsys.integration.detect;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detector.base.DetectorType;

public class DetectorTypeTest {
    @Test
    public void testFoundName() {
        Assertions.assertTrue(DetectorType.getPossibleNames().contains(DetectorType.COCOAPODS.name()));
    }

    @Test
    public void testNotFoundName() {
        Assertions.assertFalse(DetectorType.getPossibleNames().contains("quoth the raven - NEVERMORE!"));
    }

}
