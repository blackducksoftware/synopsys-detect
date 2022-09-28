package com.synopsys.integration.detect.lifecycle.boot.product.version;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.configuration.property.types.enumallnone.list.AllEnumList;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.DetectPropertyConfiguration;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;

public class BlackDuckVersionCheckerTest {

    @Test
    void testRapidSigScan() {

        DetectPropertyConfiguration config = Mockito.mock(DetectPropertyConfiguration.class);
        AllEnumList<DetectTool> detectToolsListContainingSigScan = Mockito.mock(AllEnumList.class);
        Mockito.when(detectToolsListContainingSigScan.containsValue(DetectTool.SIGNATURE_SCAN)).thenReturn(true);
        Mockito.when(config.getValue(DetectProperties.DETECT_TOOLS)).thenReturn(detectToolsListContainingSigScan);
        Mockito.when(config.getValue(DetectProperties.DETECT_BLACKDUCK_SCAN_MODE)).thenReturn(BlackduckScanMode.RAPID);

        BlackDuckMinimumVersionChecks blackDuckMinimumVersionChecks = new BlackDuckMinimumVersionChecks();
        BlackDuckVersionChecker blackDuckVersionChecker = new BlackDuckVersionChecker(new BlackDuckVersionParser(), blackDuckMinimumVersionChecks, config);

        assertTrue(blackDuckVersionChecker.check("2022.10.0").isPassed());

        assertTrue(blackDuckVersionChecker.check("2022.10.1").isPassed());
        assertFalse(blackDuckVersionChecker.check("2022.9.3").isPassed());

        assertTrue(blackDuckVersionChecker.check("2022.10.1-QA").isPassed());
        assertFalse(blackDuckVersionChecker.check("2022.9.3-SNAPSHOT").isPassed());
    }

    @Test
    void testOriginalRapid() {
        DetectPropertyConfiguration config = Mockito.mock(DetectPropertyConfiguration.class);

        AllEnumList<DetectTool> detectToolsListWithoutSigScan = Mockito.mock(AllEnumList.class);
        Mockito.when(detectToolsListWithoutSigScan.containsValue(DetectTool.SIGNATURE_SCAN)).thenReturn(false);
        Mockito.when(config.getValue(DetectProperties.DETECT_TOOLS)).thenReturn(detectToolsListWithoutSigScan);
        Mockito.when(config.getValue(DetectProperties.DETECT_BLACKDUCK_SCAN_MODE)).thenReturn(BlackduckScanMode.RAPID);

        BlackDuckMinimumVersionChecks blackDuckMinimumVersionChecks = new BlackDuckMinimumVersionChecks();
        BlackDuckVersionChecker blackDuckVersionChecker = new BlackDuckVersionChecker(new BlackDuckVersionParser(), blackDuckMinimumVersionChecks, config);

        assertTrue(blackDuckVersionChecker.check("2021.6.0").isPassed());
        assertTrue(blackDuckVersionChecker.check("2021.6.1").isPassed());
        assertFalse(blackDuckVersionChecker.check("2021.5.3").isPassed());
    }

    @Test
    void testIac() {
        DetectPropertyConfiguration config = Mockito.mock(DetectPropertyConfiguration.class);
        AllEnumList<DetectTool> detectToolsListWithoutSigScan = Mockito.mock(AllEnumList.class);
        Mockito.when(detectToolsListWithoutSigScan.containsValue(DetectTool.SIGNATURE_SCAN)).thenReturn(false);
        Mockito.when(config.getValue(DetectProperties.DETECT_TOOLS)).thenReturn(detectToolsListWithoutSigScan);
        Mockito.when(config.getValue(DetectProperties.DETECT_BLACKDUCK_SCAN_MODE)).thenReturn(BlackduckScanMode.RAPID);

        BlackDuckMinimumVersionChecks blackDuckMinimumVersionChecks = new BlackDuckMinimumVersionChecks();
        BlackDuckVersionChecker blackDuckVersionChecker = new BlackDuckVersionChecker(new BlackDuckVersionParser(), blackDuckMinimumVersionChecks, config);

        assertTrue(blackDuckVersionChecker.check("2021.6.0").isPassed());
        assertFalse(blackDuckVersionChecker.check("2021.5.3").isPassed());
    }
}
