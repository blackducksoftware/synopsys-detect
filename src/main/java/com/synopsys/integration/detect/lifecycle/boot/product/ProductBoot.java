package com.synopsys.integration.detect.lifecycle.boot.product;

import java.io.IOException;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.generated.discovery.ApiDiscovery;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.version.BlackDuckVersion;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.boot.decision.BlackDuckDecision;
import com.synopsys.integration.detect.lifecycle.boot.product.version.BlackDuckVersionChecker;
import com.synopsys.integration.detect.lifecycle.boot.product.version.BlackDuckVersionCheckerResult;
import com.synopsys.integration.detect.lifecycle.boot.product.version.BlackDuckVersionParser;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.blackduck.analytics.AnalyticsConfigurationService;
import com.synopsys.integration.detect.workflow.blackduck.analytics.AnalyticsSetting;
import com.synopsys.integration.detect.workflow.phonehome.PhoneHomeManager;
import com.synopsys.integration.exception.IntegrationException;

public class ProductBoot {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final BlackDuckConnectivityChecker blackDuckConnectivityChecker;
    private final AnalyticsConfigurationService analyticsConfigurationService;
    private final ProductBootFactory productBootFactory;
    private final ProductBootOptions productBootOptions;
    private final BlackDuckVersionChecker blackDuckVersionChecker;

    public ProductBoot(
        BlackDuckConnectivityChecker blackDuckConnectivityChecker,
        AnalyticsConfigurationService analyticsConfigurationService,
        ProductBootFactory productBootFactory,
        ProductBootOptions productBootOptions,
        BlackDuckVersionChecker blackDuckVersionChecker
    ) {
        this.blackDuckConnectivityChecker = blackDuckConnectivityChecker;
        this.analyticsConfigurationService = analyticsConfigurationService;
        this.productBootFactory = productBootFactory;
        this.productBootOptions = productBootOptions;
        this.blackDuckVersionChecker = blackDuckVersionChecker;
    }

    public ProductRunData boot(BlackDuckDecision blackDuckDecision, DetectToolFilter detectToolFilter) throws DetectUserFriendlyException {
        if (!blackDuckDecision.shouldRun()) {
            throw new DetectUserFriendlyException(
                "Your environment was not sufficiently configured to run Black Duck.  See online help at: https://sig-product-docs.synopsys.com/bundle/integrations-detect/page/introduction.html",
                ExitCodeType.FAILURE_CONFIGURATION
            );

        }

        logger.debug("Detect product boot start.");

        BlackDuckRunData blackDuckRunData = getBlackDuckRunData(
            blackDuckDecision,
            productBootFactory,
            blackDuckConnectivityChecker,
            productBootOptions,
            analyticsConfigurationService
        );

        if (productBootOptions.isTestConnections()) {
            logger.debug(String.format("%s is set to 'true' so Detect will not run.", DetectProperties.DETECT_TEST_CONNECTION.getName()));
            return null;
        }

        logger.debug("Detect product boot completed.");
        return new ProductRunData(blackDuckRunData, detectToolFilter);
    }

    @Nullable
    private BlackDuckRunData getBlackDuckRunData(
        BlackDuckDecision blackDuckDecision,
        ProductBootFactory productBootFactory,
        BlackDuckConnectivityChecker blackDuckConnectivityChecker,
        ProductBootOptions productBootOptions,
        AnalyticsConfigurationService analyticsConfigurationService
    ) throws DetectUserFriendlyException {
        if (!blackDuckDecision.shouldRun()) {
            return null;
        }

        if (blackDuckDecision.isOffline()) {
            return BlackDuckRunData.offline();
        }

        logger.debug("Will boot Black Duck product.");
        BlackDuckServerConfig blackDuckServerConfig = productBootFactory.createBlackDuckServerConfig();
        BlackDuckConnectivityResult blackDuckConnectivityResult = blackDuckConnectivityChecker.determineConnectivity(blackDuckServerConfig);

        if (blackDuckConnectivityResult.isSuccessfullyConnected()) {
            BlackDuckVersionCheckerResult blackDuckVersionCheckerResult = blackDuckVersionChecker.check(blackDuckConnectivityResult.getContactedServerVersion());
            if (!blackDuckVersionCheckerResult.isPassed()) {
                throw new DetectUserFriendlyException(
                    blackDuckVersionCheckerResult.getMessage(),
                    ExitCodeType.FAILURE_BLACKDUCK_VERSION_NOT_SUPPORTED
                );
            }
            
            BlackDuckServicesFactory blackDuckServicesFactory = blackDuckConnectivityResult.getBlackDuckServicesFactory();
            setBlackDuckVersionLevel(blackDuckServicesFactory, blackDuckConnectivityResult);
            BlackDuckRunData bdRunData = null;
            
            boolean waitAtScanLevel = shouldWaitAtScanLevel(blackDuckConnectivityResult);
            
            if (shouldUsePhoneHome(analyticsConfigurationService, blackDuckServicesFactory.getApiDiscovery(), blackDuckServicesFactory.getBlackDuckApiClient())) {
                PhoneHomeManager phoneHomeManager = productBootFactory.createPhoneHomeManager(blackDuckServicesFactory);
                bdRunData = BlackDuckRunData.online(
                    blackDuckDecision.scanMode(),
                    blackDuckServicesFactory,
                    phoneHomeManager,
                    blackDuckConnectivityResult.getBlackDuckServerConfig(),
                    waitAtScanLevel
                );
            } else {
                logger.debug("Skipping phone home due to Black Duck global settings.");
                bdRunData = BlackDuckRunData.onlineNoPhoneHome(blackDuckDecision.scanMode(), blackDuckServicesFactory, blackDuckConnectivityResult.getBlackDuckServerConfig(), waitAtScanLevel);
            }
            return bdRunData;
        } else {
            if (productBootOptions.isIgnoreConnectionFailures()) {
                logger.info(String.format("Failed to connect to Black Duck: %s", blackDuckConnectivityResult.getFailureReason()));
                logger.info(String.format(
                    "%s is set to 'true' so Detect will simply disable the Black Duck product.",
                    DetectProperties.DETECT_IGNORE_CONNECTION_FAILURES.getName()
                ));
                return null;
            } else {
                throw new DetectUserFriendlyException(
                    "Could not communicate with Black Duck: " + blackDuckConnectivityResult.getFailureReason(),
                    ExitCodeType.FAILURE_BLACKDUCK_CONNECTIVITY
                );
            }
        }
    }

    private void setBlackDuckVersionLevel(BlackDuckServicesFactory blackDuckServicesFactory,
            BlackDuckConnectivityResult blackDuckConnectivityResult) {
        BlackDuckVersionParser parser = new BlackDuckVersionParser();
        Optional<BlackDuckVersion> blackDuckServerVersion = parser.parse(blackDuckConnectivityResult.getContactedServerVersion());
        
        if (blackDuckServerVersion.isPresent()) {
            blackDuckServicesFactory.getBlackDuckApiClient().setBlackDuckVersion(blackDuckServerVersion.get());
        }  
    }

    private boolean shouldUsePhoneHome(AnalyticsConfigurationService analyticsConfigurationService, ApiDiscovery apiDiscovery, BlackDuckApiClient blackDuckService) {
        try {
            AnalyticsSetting analyticsSetting = analyticsConfigurationService.fetchAnalyticsSetting(apiDiscovery, blackDuckService);
            return analyticsSetting.isEnabled();
        } catch (IntegrationException | IOException e) {
            logger.trace("Failed to check analytics setting on Black Duck. Likely this Black Duck instance does not support it.", e);
            return true; // Skip phone home will be applied at the library level.
        }
    }
    
    private boolean shouldWaitAtScanLevel(BlackDuckConnectivityResult blackDuckConnectivityResult) {
        BlackDuckVersionParser parser = new BlackDuckVersionParser();
        Optional<BlackDuckVersion> blackDuckServerVersion = parser.parse(blackDuckConnectivityResult.getContactedServerVersion());
        BlackDuckVersion minVersion = new BlackDuckVersion(2023, 1, 1);
        
        boolean waitAtScanLevel = false;
        
        if (blackDuckServerVersion.isPresent() && blackDuckServerVersion.get().isAtLeast(minVersion)) {
            waitAtScanLevel = true;
        }
        
        return waitAtScanLevel;
    }
}
