package com.blackducksoftware.integration.hub.detect.project.result;

import org.slf4j.Logger;

public class NoUniqueUnchosenProjectInfoResult extends ProjectInfoResult {
    @Override
    public void printDescription(final Logger logger) {
        logger.info("No unique bom tool was found. Project info could not be found in a bom tool.");
    }

}
