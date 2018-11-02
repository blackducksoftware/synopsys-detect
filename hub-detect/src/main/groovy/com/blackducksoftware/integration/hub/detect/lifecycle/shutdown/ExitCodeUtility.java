package com.blackducksoftware.integration.hub.detect.lifecycle.shutdown;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.synopsys.integration.exception.IntegrationException;

public class ExitCodeUtility {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ExitCodeType getExitCodeFromExceptionDetails(final Exception e) {
        final ExitCodeType exceptionExitCodeType;

        if (e instanceof DetectUserFriendlyException) {
            if (e.getCause() != null) {
                logger.debug(e.getCause().getMessage(), e.getCause());
            }
            final DetectUserFriendlyException friendlyException = (DetectUserFriendlyException) e;
            exceptionExitCodeType = friendlyException.getExitCodeType();
        } else if (e instanceof IntegrationException) {
            logger.error("An unrecoverable error occurred - most likely this is due to your environment and/or configuration. Please double check the Detect documentation: https://blackducksoftware.atlassian.net/wiki/x/Y7HtAg");
            logger.debug(e.getMessage(), e);
            exceptionExitCodeType = ExitCodeType.FAILURE_GENERAL_ERROR;
        } else {
            logger.error("An unknown/unexpected error occurred");
            if (e.getMessage() != null) {
                logger.error(e.getMessage());
            } else if (e instanceof NullPointerException) {
                logger.error("Null Pointer Exception");
            } else {
                logger.error(e.getClass().getSimpleName());
            }
            if (e.getStackTrace().length >= 1) {
                logger.error("Thrown at " + e.getStackTrace()[0].toString());
            }
            exceptionExitCodeType = ExitCodeType.FAILURE_UNKNOWN_ERROR;
        }
        logger.debug("Exception", e);

        return exceptionExitCodeType;
    }
}
