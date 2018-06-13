package com.blackducksoftware.integration.hub.detect.project.result;

import java.util.Optional;

import org.slf4j.Logger;

import com.blackducksoftware.integration.hub.detect.project.BomToolProjectInfo;
import com.blackducksoftware.integration.util.NameVersion;

public class PreferredProjectInfoResult extends ProjectInfoResult {

    private final BomToolProjectInfo chosenBomToolProjectInfo;

    public PreferredProjectInfoResult(final BomToolProjectInfo chosenBomToolProjectInfo) {
        this.chosenBomToolProjectInfo = chosenBomToolProjectInfo;
    }

    @Override
    public Optional<NameVersion> getChosenNameVersion() {
        return Optional.of(chosenBomToolProjectInfo.getNameVersion());
    }

    @Override
    public boolean didChoose() {
        return true;
    }

    @Override
    public void printDescription(final Logger logger) {
        logger.info("Using preferred bom tool project info from " + chosenBomToolProjectInfo.getBomToolType().toString() + " found at depth " + Integer.toString(chosenBomToolProjectInfo.getDepth()) + " as project info.");
    }

}
