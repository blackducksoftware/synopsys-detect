package com.synopsys.integration.detect.lifecycle.boot.decision;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.connection.BlackDuckConnectionDetails;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;

public class ProductDecider {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public BlackDuckDecision decideBlackDuck(BlackDuckConnectionDetails blackDuckConnectionDetails, BlackduckScanMode scanMode, boolean hasSigScan) {
        boolean offline = blackDuckConnectionDetails.getOffline();
        Optional<String> blackDuckUrl = blackDuckConnectionDetails.getBlackDuckUrl();
        if (offline && (BlackduckScanMode.RAPID.equals(scanMode) || BlackduckScanMode.STATELESS.equals(scanMode))) {
            logger.debug("Black Duck will NOT run: Rapid mode cannot be run offline.");
            return BlackDuckDecision.skip();
        } else if (offline) {
            logger.debug("Black Duck will run: Black Duck offline mode was set to true.");
            return BlackDuckDecision.runOffline();
        } else if (blackDuckUrl.isPresent()) {
            logger.debug("Black Duck will run ONLINE: A Black Duck url was found.");
            return BlackDuckDecision.runOnline(scanMode, hasSigScan);
        } else {
            logger.debug("Black Duck will NOT run: The Black Duck url must be provided or offline mode must be set to true.");
            return BlackDuckDecision.skip();
        }
    }

}
