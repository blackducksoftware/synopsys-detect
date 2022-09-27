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
        BlackDuckMinimumVersionChecks blackDuckMinimumVersionChecks = new BlackDuckMinimumVersionChecks();
        BlackDuckVersionChecker blackDuckVersionChecker = new BlackDuckVersionChecker(new BlackDuckVersionParser(), blackDuckMinimumVersionChecks);

        DetectPropertyConfiguration config = Mockito.mock(DetectPropertyConfiguration.class);
        AllEnumList<DetectTool> detectToolsListContainingSigScan = Mockito.mock(AllEnumList.class);
        Mockito.when(detectToolsListContainingSigScan.containsValue(DetectTool.SIGNATURE_SCAN)).thenReturn(true);
        Mockito.when(config.getValue(DetectProperties.DETECT_TOOLS)).thenReturn(detectToolsListContainingSigScan);
        Mockito.when(config.getValue(DetectProperties.DETECT_BLACKDUCK_SCAN_MODE)).thenReturn(BlackduckScanMode.RAPID);

        assertTrue(blackDuckVersionChecker.check(config, "2022.10.0"));

        assertTrue(blackDuckVersionChecker.check(config, "2022.10.1"));
        assertFalse(blackDuckVersionChecker.check(config, "2022.9.3"));

        assertTrue(blackDuckVersionChecker.check(config, "2022.10.1-QA"));
        assertFalse(blackDuckVersionChecker.check(config, "2022.9.3-SNAPSHOT"));
    }

    @Test
    void testOriginalRapid() {
        BlackDuckMinimumVersionChecks blackDuckMinimumVersionChecks = new BlackDuckMinimumVersionChecks();
        BlackDuckVersionChecker blackDuckVersionChecker = new BlackDuckVersionChecker(new BlackDuckVersionParser(), blackDuckMinimumVersionChecks);

        DetectPropertyConfiguration config = Mockito.mock(DetectPropertyConfiguration.class);

        AllEnumList<DetectTool> detectToolsListWithoutSigScan = Mockito.mock(AllEnumList.class);
        Mockito.when(detectToolsListWithoutSigScan.containsValue(DetectTool.SIGNATURE_SCAN)).thenReturn(false);
        Mockito.when(config.getValue(DetectProperties.DETECT_TOOLS)).thenReturn(detectToolsListWithoutSigScan);
        Mockito.when(config.getValue(DetectProperties.DETECT_BLACKDUCK_SCAN_MODE)).thenReturn(BlackduckScanMode.RAPID);

        assertTrue(blackDuckVersionChecker.check(config, "2021.6.0"));
        assertTrue(blackDuckVersionChecker.check(config, "2021.6.1"));
        assertFalse(blackDuckVersionChecker.check(config, "2021.5.3"));
    }

    @Test
    void testIac() {
        BlackDuckMinimumVersionChecks blackDuckMinimumVersionChecks = new BlackDuckMinimumVersionChecks();
        BlackDuckVersionChecker blackDuckVersionChecker = new BlackDuckVersionChecker(new BlackDuckVersionParser(), blackDuckMinimumVersionChecks);

        DetectPropertyConfiguration config = Mockito.mock(DetectPropertyConfiguration.class);
        AllEnumList<DetectTool> detectToolsListWithoutSigScan = Mockito.mock(AllEnumList.class);
        Mockito.when(detectToolsListWithoutSigScan.containsValue(DetectTool.SIGNATURE_SCAN)).thenReturn(false);
        Mockito.when(config.getValue(DetectProperties.DETECT_TOOLS)).thenReturn(detectToolsListWithoutSigScan);
        Mockito.when(config.getValue(DetectProperties.DETECT_BLACKDUCK_SCAN_MODE)).thenReturn(BlackduckScanMode.RAPID);

        assertTrue(blackDuckVersionChecker.check(config, "2021.6.0"));
        assertFalse(blackDuckVersionChecker.check(config, "2021.5.3"));
    }
}
