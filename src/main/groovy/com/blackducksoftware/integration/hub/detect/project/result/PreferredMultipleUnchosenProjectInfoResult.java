package com.blackducksoftware.integration.hub.detect.project.result;

import java.util.Optional;

import org.slf4j.Logger;

import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.util.NameVersion;

public class PreferredMultipleUnchosenProjectInfoResult extends ProjectInfoResult {
    private final BomToolType bomToolType;

    public PreferredMultipleUnchosenProjectInfoResult(final BomToolType bomToolType) {
        this.bomToolType = bomToolType;
    }

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
        logger.info("More than one preferred bom tool of type " + bomToolType.toString() + " was found. Project info could not be found in a bom tool.");
    }
}