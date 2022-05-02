package com.synopsys.integration.detect.boot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.configuration.connection.BlackDuckConnectionDetails;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.lifecycle.boot.decision.BlackDuckDecision;
import com.synopsys.integration.detect.lifecycle.boot.decision.ProductDecider;
import com.synopsys.integration.detect.workflow.bdio.BdioOptions;

class ProductDeciderTest {
    private final String VALID_URL = "http://example";

    @Test
    public void shouldRunBlackDuckOfflineWhenOverride() {
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(true, null);
        BlackDuckDecision productDecision = new ProductDecider().decideBlackDuck(
            blackDuckConnectionDetails,
            BlackduckScanMode.INTELLIGENT,
            createBdioOptions(true)
        );

        Assertions.assertTrue(productDecision.shouldRun());
        Assertions.assertTrue(productDecision.isOffline());
    }

    @Test
    public void shouldRunOfflineEvenWhenUrlProvided() {
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(true, "http://example.com");
        BlackDuckDecision productDecision = new ProductDecider().decideBlackDuck(
            blackDuckConnectionDetails,
            BlackduckScanMode.INTELLIGENT,
            createBdioOptions(true)
        );

        Assertions.assertTrue(productDecision.shouldRun());
        Assertions.assertTrue(productDecision.isOffline());
    }

    @Test
    public void shouldRunBlackDuckOfflineWhenInstallUrl() {
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(true, null);
        BlackDuckDecision productDecision = new ProductDecider().decideBlackDuck(
            blackDuckConnectionDetails,
            BlackduckScanMode.INTELLIGENT,
            createBdioOptions(false)
        );

        Assertions.assertTrue(productDecision.shouldRun());
        Assertions.assertTrue(productDecision.isOffline());
    }

    @Test
    public void shouldRunBlackDuckOfflineWhenInstallPath() {
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(true, null);
        BlackDuckDecision productDecision = new ProductDecider().decideBlackDuck(
            blackDuckConnectionDetails,
            BlackduckScanMode.INTELLIGENT,
            createBdioOptions(false)
        );

        Assertions.assertTrue(productDecision.shouldRun());
        Assertions.assertTrue(productDecision.isOffline());
    }

    @Test
    public void shouldNotRunBlackDuckOfflineWhenUserProvidedHostUrl() {
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(true, null);
        BlackDuckDecision productDecision = new ProductDecider().decideBlackDuck(
            blackDuckConnectionDetails,
            BlackduckScanMode.INTELLIGENT,
            createBdioOptions(false)
        );

        Assertions.assertTrue(productDecision.shouldRun());
        Assertions.assertTrue(productDecision.isOffline());
    }

    @Test
    public void shouldRunBlackDuckOnline() {
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(false, VALID_URL);
        BlackDuckDecision productDecision = new ProductDecider().decideBlackDuck(
            blackDuckConnectionDetails,
            BlackduckScanMode.INTELLIGENT,
            createBdioOptions(true)
        );

        Assertions.assertTrue(productDecision.shouldRun());
        Assertions.assertFalse(productDecision.isOffline());
    }

    @Test
    public void shouldNotRunBlackduckRapidModeAndOffline() {
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(true, VALID_URL);
        BlackDuckDecision productDecision = new ProductDecider().decideBlackDuck(
            blackDuckConnectionDetails,
            BlackduckScanMode.RAPID,
            createBdioOptions(false)
        );

        Assertions.assertFalse(productDecision.shouldRun());
    }

    @Test
    public void shouldNotRunBlackduckRapidModeAndBDIO2Disabled() {
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(false, VALID_URL);
        BlackDuckDecision productDecision = new ProductDecider().decideBlackDuck(
            blackDuckConnectionDetails,
            BlackduckScanMode.RAPID,
            createBdioOptions(false)
        );

        Assertions.assertFalse(productDecision.shouldRun());
    }

    @Test
    public void shouldNotRunBlackduckIntelligentModeAndBDIO2Disabled() {
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(false, VALID_URL);
        BlackDuckDecision productDecision = new ProductDecider().decideBlackDuck(
            blackDuckConnectionDetails,
            BlackduckScanMode.INTELLIGENT,
            createBdioOptions(false)
        );

        Assertions.assertFalse(productDecision.shouldRun());
    }

    @Test
    public void shouldRunBlackduckRapidModeAndBDIO2Enabled() {
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(false, VALID_URL);
        BlackDuckDecision productDecision = new ProductDecider().decideBlackDuck(
            blackDuckConnectionDetails,
            BlackduckScanMode.RAPID,
            createBdioOptions(true)
        );

        Assertions.assertTrue(productDecision.shouldRun());
    }

    @Test
    public void something() {
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(true, VALID_URL);
        BlackDuckDecision productDecision = new ProductDecider().decideBlackDuck(
            blackDuckConnectionDetails,
            BlackduckScanMode.INTELLIGENT,
            createBdioOptions(false)
        );

        Assertions.assertTrue(productDecision.shouldRun());
        Assertions.assertTrue(productDecision.isOffline());
    }

    @Test
    public void shouldRunBlackduckIntelligentModeAndBDIO2Enabled() {
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(false, VALID_URL);
        BlackDuckDecision productDecision = new ProductDecider().decideBlackDuck(
            blackDuckConnectionDetails,
            BlackduckScanMode.INTELLIGENT,
            createBdioOptions(true)
        );

        Assertions.assertTrue(productDecision.shouldRun());
    }

    @Test
    public void shouldRunBlackduckLegacyEnabledAndIntelligentModeAndBDIO2Disabled() {
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(false, VALID_URL);
        BlackDuckDecision productDecision = new ProductDecider().decideBlackDuck(
            blackDuckConnectionDetails,
            BlackduckScanMode.INTELLIGENT,
            createBdioOptions(true)
        );

        Assertions.assertTrue(productDecision.shouldRun());
    }

    @Test
    public void shouldNotRunBlackduckURLMissing() {
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(false, null);
        BlackDuckDecision productDecision = new ProductDecider().decideBlackDuck(
            blackDuckConnectionDetails,
            BlackduckScanMode.RAPID,
            createBdioOptions(true)
        );

        Assertions.assertFalse(productDecision.shouldRun());
    }

    private BdioOptions createBdioOptions(boolean useBdio2) {
        return new BdioOptions(useBdio2, null, null);
    }

    private BlackDuckConnectionDetails blackDuckConnectionDetails(boolean offline, String blackduckUrl) {
        return new BlackDuckConnectionDetails(offline, blackduckUrl, null, null, null);
    }
}
