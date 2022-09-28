package com.synopsys.integration.detect.lifecycle.boot.product;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.generated.discovery.ApiDiscovery;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.boot.decision.BlackDuckDecision;
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

    private final java.util.regex.Pattern versionPattern = Pattern.compile("^([0-9]{4})\\.(\\d+)\\..*?");
    private static final int MAJOR_VERSION = 2022;
    private static final int MINOR_VERSION = 10;

    public ProductBoot(
        BlackDuckConnectivityChecker blackDuckConnectivityChecker,
        AnalyticsConfigurationService analyticsConfigurationService,
        ProductBootFactory productBootFactory,
        ProductBootOptions productBootOptions
    ) {
        this.blackDuckConnectivityChecker = blackDuckConnectivityChecker;
        this.analyticsConfigurationService = analyticsConfigurationService;
        this.productBootFactory = productBootFactory;
        this.productBootOptions = productBootOptions;
    }

    public ProductRunData boot(BlackDuckDecision blackDuckDecision, DetectToolFilter detectToolFilter) throws DetectUserFriendlyException {
        if (!blackDuckDecision.shouldRun()) {
            throw new DetectUserFriendlyException(
                "Your environment was not sufficiently configured to run Black Duck.  See online help at: https://detect.synopsys.com/doc/",
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
            BlackDuckServicesFactory blackDuckServicesFactory = blackDuckConnectivityResult.getBlackDuckServicesFactory();

            BlackDuckRunData bdRunData= null;
            if (shouldUsePhoneHome(analyticsConfigurationService, blackDuckServicesFactory.getApiDiscovery(), blackDuckServicesFactory.getBlackDuckApiClient())) {
                PhoneHomeManager phoneHomeManager = productBootFactory.createPhoneHomeManager(blackDuckServicesFactory);
                bdRunData = BlackDuckRunData.online(blackDuckDecision.scanMode(), blackDuckServicesFactory, phoneHomeManager, blackDuckConnectivityResult.getBlackDuckServerConfig());
            } else {
                logger.debug("Skipping phone home due to Black Duck global settings.");
                bdRunData = BlackDuckRunData.onlineNoPhoneHome(blackDuckDecision.scanMode(), blackDuckServicesFactory, blackDuckConnectivityResult.getBlackDuckServerConfig());
            }
            if (bdRunData.isRapid() && blackDuckDecision.hasSignatureScan()) {
                String bdVersion = blackDuckConnectivityResult.getContactedServerVersion();
                if (!isServerVersionSufficient(bdVersion)) {
                    // abort!
                    throw new DetectUserFriendlyException(
                            "Cannot use RAPID SIGNATURE SCAN with Black Duck Versions prior to 2022.10.0!  The Black Duck Version attempted was: " + bdVersion,
                            ExitCodeType.FAILURE_BLACKDUCK_CONNECTIVITY
                        );
                }
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

    private boolean shouldUsePhoneHome(AnalyticsConfigurationService analyticsConfigurationService, ApiDiscovery apiDiscovery, BlackDuckApiClient blackDuckService) {
        try {
            AnalyticsSetting analyticsSetting = analyticsConfigurationService.fetchAnalyticsSetting(apiDiscovery, blackDuckService);
            return analyticsSetting.isEnabled();
        } catch (IntegrationException | IOException e) {
            logger.trace("Failed to check analytics setting on Black Duck. Likely this Black Duck instance does not support it.", e);
            return true; // Skip phone home will be applied at the library level.
        }
    }

    private boolean isServerVersionSufficient(String version) {
        // relies on version string being YYYY.MM.N etc. Since minimum version
        // is 2022.10.0 we should only need to check major and middle.  A pattern
        // match will be done to ensure that the form is suitable for a match.  If
        // the pattern changes in the future, we're almost, but not quite, guaranteed that the 
        // blackduck version will be sufficient!  If the pattern doesn't match in the
        // past, we'll pass it along and let it fail outright.

        Matcher m = versionPattern.matcher(version);
        if (!m.matches()) {
            return true;
        }

        String[] parts = {m.group(1),m.group(2)};//version.split("\\.");

        // we only need to check the first two parts since minimal minor version is ZERO
        // we are guaranteed that parts will be integers from the pattern match at this point.
        int major_version_from_bd = Integer.parseInt(parts[0]);
        int minor_version_from_bd = Integer.parseInt(parts[1]);

        if (major_version_from_bd > MAJOR_VERSION) {
            return true; 
        } else if (major_version_from_bd == MAJOR_VERSION && minor_version_from_bd >= MINOR_VERSION) {
            return true;
        }

        return false;
    }
}
