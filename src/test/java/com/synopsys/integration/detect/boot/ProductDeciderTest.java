package com.synopsys.integration.detect.boot;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.configuration.connection.BlackDuckConnectionDetails;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.lifecycle.boot.decision.BlackDuckDecision;
import com.synopsys.integration.detect.lifecycle.boot.decision.ProductDecider;

class ProductDeciderTest {
    private final String VALID_URL = "https://example.com";

    @Test
    public void shouldRunIntelligentOffline() {
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(true, null);
        BlackDuckDecision productDecision = new ProductDecider().decideBlackDuck(
            blackDuckConnectionDetails,
            BlackduckScanMode.INTELLIGENT
        );

        assertOfflineDecision(productDecision);
    }

    @Test
    public void shouldRunIntelligentOfflineEvenWhenUrlProvided() {
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(true, VALID_URL);
        BlackDuckDecision productDecision = new ProductDecider().decideBlackDuck(
            blackDuckConnectionDetails,
            BlackduckScanMode.INTELLIGENT
        );

        assertOfflineDecision(productDecision);
    }

    @Test
    public void shouldRunIntelligentOnline() {
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(false, VALID_URL);
        BlackDuckDecision productDecision = new ProductDecider().decideBlackDuck(
            blackDuckConnectionDetails,
            BlackduckScanMode.INTELLIGENT
        );

        assertOnlineDecision(productDecision);
    }

    @Test
    public void shouldNotRunIntelligentOnlineWhenMissingURL() {
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(false, null);
        BlackDuckDecision productDecision = new ProductDecider().decideBlackDuck(
            blackDuckConnectionDetails,
            BlackduckScanMode.INTELLIGENT
        );

        assertSkipDecision(productDecision);
    }

    @Test
    public void shouldNotRunRapidOffline() {
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(true, VALID_URL);
        BlackDuckDecision productDecision = new ProductDecider().decideBlackDuck(
            blackDuckConnectionDetails,
            BlackduckScanMode.RAPID
        );

        assertSkipDecision(productDecision);
    }

    @Test
    public void shouldRunRapidOnline() {
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(false, VALID_URL);
        BlackDuckDecision productDecision = new ProductDecider().decideBlackDuck(
            blackDuckConnectionDetails,
            BlackduckScanMode.RAPID
        );

        assertOnlineDecision(productDecision);
    }

    @Test
    public void shouldNotRunRapidWhenMissingURL() {
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(false, null);
        BlackDuckDecision productDecision = new ProductDecider().decideBlackDuck(
            blackDuckConnectionDetails,
            BlackduckScanMode.RAPID
        );

        assertSkipDecision(productDecision);
    }

    @Test
    public void shouldNotRunRapidWhenMissingURLEvenOffline() {
        BlackDuckConnectionDetails blackDuckConnectionDetails = blackDuckConnectionDetails(true, null);
        BlackDuckDecision productDecision = new ProductDecider().decideBlackDuck(
            blackDuckConnectionDetails,
            BlackduckScanMode.RAPID
        );

        assertSkipDecision(productDecision);
    }

    private void assertOfflineDecision(BlackDuckDecision productDecision) {
        assertTrue(productDecision.shouldRun(), "An offline decision is validated and should be cleared to run");
        assertTrue(productDecision.isOffline(), "An offline run decision should be reported as 'offline'");
    }

    private void assertOnlineDecision(BlackDuckDecision productDecision) {
        assertTrue(productDecision.shouldRun(), "An online decision is validated and should be cleared to run");
        assertFalse(productDecision.isOffline(), "An online decision is should not be 'offline'");
    }

    private void assertSkipDecision(BlackDuckDecision productDecision) {
        assertFalse(productDecision.shouldRun(), "A skip decision should not be run");
        assertTrue(productDecision.isOffline(), "A skip decision is offline");
    }

    private BlackDuckConnectionDetails blackDuckConnectionDetails(boolean offline, String blackduckUrl) {
        return new BlackDuckConnectionDetails(offline, blackduckUrl, null, null, null);
    }
}
