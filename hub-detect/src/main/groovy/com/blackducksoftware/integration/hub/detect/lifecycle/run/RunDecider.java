package com.blackducksoftware.integration.hub.detect.lifecycle.run;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.tool.polaris.PolarisEnvironmentCheck;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;

public class RunDecider {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public RunDecision decide(DetectConfiguration detectConfiguration, PolarisEnvironmentCheck polarisEnvironmentCheck, DirectoryManager directoryManager) {
        boolean runBlackduck = false;
        boolean runPolaris = false;
        boolean offline = detectConfiguration.getBooleanProperty(DetectProperty.BLACKDUCK_OFFLINE_MODE, PropertyAuthority.None);
        String hubUrl = detectConfiguration.getProperty(DetectProperty.BLACKDUCK_URL, PropertyAuthority.None);
        if (offline || StringUtils.isNotBlank(hubUrl)) {
            logger.info("Either the Black Duck url was found or offline mode is set, will run Black Duck product.");
            runBlackduck = true;
        } else {
            logger.info("No Black Duck url was found and offline mode is not set, will NOT run Black Duck product.");
        }
        String polarisUrl = detectConfiguration.getProperty(DetectProperty.POLARIS_URL, PropertyAuthority.None);
        boolean libraryCanRun = polarisEnvironmentCheck.canRun(directoryManager.getUserHome());
        if (StringUtils.isBlank(polarisUrl)) {
            logger.info("No polaris url was found, cannot run polaris.");
        } else if (!libraryCanRun) {
            logger.info("No polaris access token was found even though a url was found, cannot run polaris.");
            runPolaris = true;
        } else {
            logger.info("A polaris access token and url was found, will run Polaris product.");
            runPolaris = true;
        }

        return new RunDecision(runBlackduck, runPolaris);
    }
}
