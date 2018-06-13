package com.blackducksoftware.integration.hub.detect.project.result;

import java.util.Optional;

import org.slf4j.Logger;

import com.blackducksoftware.integration.util.NameVersion;

public class NoUniqueUnchosenProjectInfoResult extends ProjectInfoResult {

    @Override
    public Optional<NameVersion> getChosenNameVersion() {
        return Optional.empty();
    }

    @Override
    public boolean didChoose() {
        return false;
    }

    @Override
    public void printDescription(final Logger logger) {
        logger.info("No unique bom tool was found. Project info could not be found in a bom tool.");
    }

}
