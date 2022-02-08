package com.synopsys.integration.detect.lifecycle.boot.decision;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.connection.BlackDuckConnectionDetails;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;
import com.synopsys.integration.detect.workflow.bdio.BdioOptions;

public class ProductDecider {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public BlackDuckDecision decideBlackDuck(BlackDuckConnectionDetails blackDuckConnectionDetails, BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions, BlackduckScanMode scanMode, BdioOptions bdioOptions) {
        boolean offline = blackDuckConnectionDetails.getOffline();
        Optional<String> blackDuckUrl = blackDuckConnectionDetails.getBlackDuckUrl();
        if (scanMode == BlackduckScanMode.RAPID && !bdioOptions.isBdio2Enabled()) {
            logger.debug("Black Duck will NOT run: Detect will not generate BDIO2 files and Black Duck {} scan is enabled which requires BDIO2 file generation", scanMode.name());
            return BlackDuckDecision.skip();
        } else if (scanMode == BlackduckScanMode.INTELLIGENT && !bdioOptions.isBdio2Enabled() && !bdioOptions.isLegacyUploadEnabled()) {
            logger.debug("Black Duck will NOT run: Detect will not generate BDIO2 files and Black Duck {} scan is enabled which requires BDIO2 file generation", scanMode.name());
            return BlackDuckDecision.skip();
        } else if (offline) {
            logger.debug("Black Duck will run: Black Duck offline mode was set to true.");
            return BlackDuckDecision.runOffline();
        } else if (blackDuckUrl.isPresent()) {
            logger.debug("Black Duck will run ONLINE: A Black Duck url was found.");
            return BlackDuckDecision.runOnline(scanMode);
        } else {
            logger.debug("Black Duck will NOT run: The Black Duck url must be provided or offline mode must be set to true.");
            return BlackDuckDecision.skip();
        }
    }

}
