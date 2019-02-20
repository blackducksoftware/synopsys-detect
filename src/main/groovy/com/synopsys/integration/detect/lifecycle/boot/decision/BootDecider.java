package com.synopsys.integration.detect.lifecycle.boot.decision;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.help.DetectOptionManager;
import com.synopsys.integration.detect.lifecycle.boot.BlackDuckConnectivityChecker;
import com.synopsys.integration.detect.lifecycle.boot.BlackDuckConnectivityResult;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfig;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfigBuilder;
import com.synopsys.integration.util.BuilderStatus;

public class BootDecider {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private PolarisServerConfigBuilder createPolarisServerConfigBuilder(DetectConfiguration detectConfiguration, DirectoryManager directoryManager) {
        PolarisServerConfigBuilder polarisServerConfigBuilder = PolarisServerConfig.newBuilder();
        Set<String> allPolarisKeys = new HashSet<>(polarisServerConfigBuilder.getAllPropertyKeys());
        Map<String, String> polarisProperties = detectConfiguration.getProperties(allPolarisKeys);
        polarisServerConfigBuilder.setFromProperties(polarisProperties);
        polarisServerConfigBuilder.setUserHomePath(directoryManager.getUserHome().getAbsolutePath());
        polarisServerConfigBuilder.setTimeoutSeconds(120);
        return polarisServerConfigBuilder;
    }

    private PolarisDecision determinePolaris(DetectConfiguration detectConfiguration, DirectoryManager directoryManager){
        PolarisServerConfigBuilder polarisServerConfigBuilder = createPolarisServerConfigBuilder(detectConfiguration, directoryManager);
        BuilderStatus builderStatus = polarisServerConfigBuilder.validateAndGetBuilderStatus();
        boolean polarisCanRun = builderStatus.isValid();

        if (!polarisCanRun) {
            logger.info("The supplied properties are not sufficient to run polaris - polaris will not run.");
            logger.info(builderStatus.getFullErrorMessage());
            return PolarisDecision.forSkipPolaris();
        } else {
            logger.info("A polaris access token and url were found, will run Polaris product.");
            return PolarisDecision.forOnline(polarisServerConfigBuilder.build());
        }
    }

    private BlackDuckDecision determineBlackDuck(DetectConfiguration detectConfiguration, DetectOptionManager detectOptionManager) throws DetectUserFriendlyException {
        boolean offline = detectConfiguration.getBooleanProperty(DetectProperty.BLACKDUCK_OFFLINE_MODE, PropertyAuthority.None);
        String hubUrl = detectConfiguration.getProperty(DetectProperty.BLACKDUCK_URL, PropertyAuthority.None);
        if (offline) {
            return BlackDuckDecision.forOffline();
        } else if(StringUtils.isNotBlank(hubUrl)) {
            logger.info("Either the Black Duck url was found or offline mode is set, will run Black Duck product.");

            BlackDuckServerConfig blackDuckServerConfig = detectOptionManager.createBlackduckServerConfig();
            BlackDuckConnectivityChecker connectivityChecker = new BlackDuckConnectivityChecker();
            BlackDuckConnectivityResult connectivityResult = connectivityChecker.determineConnectivity(blackDuckServerConfig);

            if (connectivityResult.isSuccessfullyConnected()){
                BlackDuckServicesFactory blackDuckServicesFactory = connectivityResult.getBlackDuckServicesFactory();
                blackDuckServerConfig = connectivityResult.getBlackDuckServerConfig();
                return BlackDuckDecision.forOnlineConnected(blackDuckServicesFactory, blackDuckServerConfig);
            } else {
                return BlackDuckDecision.forOnlineNotConnected();
            }
        } else {
            logger.info("No Black Duck url was found and offline mode is not set, will NOT run Black Duck product.");
            return BlackDuckDecision.forSkipBlackduck();
        }
    }

    public BootDecision decide(DetectConfiguration detectConfiguration, DetectOptionManager detectOptionManager, DirectoryManager directoryManager) throws DetectUserFriendlyException {
        return new BootDecision(determineBlackDuck(detectConfiguration, detectOptionManager), determinePolaris(detectConfiguration, directoryManager));
    }
}
