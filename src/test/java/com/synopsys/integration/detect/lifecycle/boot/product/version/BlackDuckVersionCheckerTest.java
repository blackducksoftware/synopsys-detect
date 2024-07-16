package com.synopsys.integration.detect.lifecycle.boot.product.version;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;

class BlackDuckVersionCheckerTest {

    @Test
    void testRapidSigScanStateless() {
        DetectToolFilter detectToolFilter = Mockito.mock(DetectToolFilter.class);
        Mockito.when(detectToolFilter.shouldInclude(DetectTool.SIGNATURE_SCAN)).thenReturn(true);
        Mockito.when(detectToolFilter.shouldInclude(DetectTool.IAC_SCAN)).thenReturn(false);
        BlackduckScanMode blackduckScanMode = BlackduckScanMode.STATELESS;
        BlackDuckVersionSensitiveOptions blackDuckVersionSensitiveOptions = new BlackDuckVersionSensitiveOptions(detectToolFilter, blackduckScanMode);

        BlackDuckMinimumVersionChecks blackDuckMinimumVersionChecks = new BlackDuckMinimumVersionChecks();
        BlackDuckVersionChecker blackDuckVersionChecker = new BlackDuckVersionChecker(new BlackDuckVersionParser(), blackDuckMinimumVersionChecks, blackDuckVersionSensitiveOptions);

        assertTrue(blackDuckVersionChecker.check("2022.10.0").isPassed());

        assertTrue(blackDuckVersionChecker.check("2022.10.1").isPassed());
        assertFalse(blackDuckVersionChecker.check("2022.9.3").isPassed());

        assertTrue(blackDuckVersionChecker.check("2022.10.1-QA").isPassed());
        assertFalse(blackDuckVersionChecker.check("2022.9.3-SNAPSHOT").isPassed());

        assertTrue(blackDuckVersionChecker.check("nonsense").isPassed());
        assertTrue(blackDuckVersionChecker.check("").isPassed());
        assertTrue(blackDuckVersionChecker.check(null).isPassed());
    }

    @Test
    void testOriginalRapid() {
        DetectToolFilter detectToolFilter = Mockito.mock(DetectToolFilter.class);
        Mockito.when(detectToolFilter.shouldInclude(DetectTool.SIGNATURE_SCAN)).thenReturn(false);
        Mockito.when(detectToolFilter.shouldInclude(DetectTool.IAC_SCAN)).thenReturn(false);
        BlackduckScanMode blackduckScanMode = BlackduckScanMode.RAPID;
        BlackDuckVersionSensitiveOptions blackDuckVersionSensitiveOptions = new BlackDuckVersionSensitiveOptions(detectToolFilter, blackduckScanMode);

        BlackDuckMinimumVersionChecks blackDuckMinimumVersionChecks = new BlackDuckMinimumVersionChecks();
        BlackDuckVersionChecker blackDuckVersionChecker = new BlackDuckVersionChecker(new BlackDuckVersionParser(), blackDuckMinimumVersionChecks, blackDuckVersionSensitiveOptions);

        assertTrue(blackDuckVersionChecker.check("2021.6.0").isPassed());
        assertTrue(blackDuckVersionChecker.check("2021.6.1").isPassed());
        assertFalse(blackDuckVersionChecker.check("2021.5.3").isPassed());
    }

    @Test
    void testIac() {
        DetectToolFilter detectToolFilter = Mockito.mock(DetectToolFilter.class);
        Mockito.when(detectToolFilter.shouldInclude(DetectTool.IAC_SCAN)).thenReturn(true);
        Mockito.when(detectToolFilter.shouldInclude(DetectTool.SIGNATURE_SCAN)).thenReturn(false);
        BlackduckScanMode blackduckScanMode = BlackduckScanMode.INTELLIGENT;
        BlackDuckVersionSensitiveOptions blackDuckVersionSensitiveOptions = new BlackDuckVersionSensitiveOptions(detectToolFilter, blackduckScanMode);

        BlackDuckMinimumVersionChecks blackDuckMinimumVersionChecks = new BlackDuckMinimumVersionChecks();
        BlackDuckVersionChecker blackDuckVersionChecker = new BlackDuckVersionChecker(new BlackDuckVersionParser(), blackDuckMinimumVersionChecks, blackDuckVersionSensitiveOptions);

        assertTrue(blackDuckVersionChecker.check("2021.6.0").isPassed());
        assertFalse(blackDuckVersionChecker.check("2021.5.3").isPassed());
    }
}
