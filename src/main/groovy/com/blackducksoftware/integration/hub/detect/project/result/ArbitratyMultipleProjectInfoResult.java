package com.blackducksoftware.integration.hub.detect.project.result;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;

import com.blackducksoftware.integration.hub.detect.project.BomToolProjectInfo;
import com.blackducksoftware.integration.util.NameVersion;

public class ArbitratyMultipleProjectInfoResult extends ProjectInfoResult {

    private final BomToolProjectInfo chosenBomTool;
    private final List<BomToolProjectInfo> otherBomTools;

    public ArbitratyMultipleProjectInfoResult(final BomToolProjectInfo chosenBomTool, final List<BomToolProjectInfo> otherBomTools) {
        this.chosenBomTool = chosenBomTool;
        this.otherBomTools = otherBomTools;
    }

    @Override
    public Optional<NameVersion> getChosenNameVersion() {
        return Optional.of(chosenBomTool.getNameVersion());
    }

    @Override
    public boolean didChoose() {
        return true;
    }

    @Override
    public void printDescription(final Logger logger) {
        logger.info("Multiple unique bom tool types were found.");
        logger.info("The following project names were found: ");
        for (final BomToolProjectInfo projectNamePossibility : otherBomTools) {
            logger.info(projectNamePossibility.getBomToolType().toString() + ": " + projectNamePossibility.getNameVersion().getName());
        }
        logger.info("Chose to use " + chosenBomTool.getBomToolType() + " at depth " + chosenBomTool.getDepth() + " for project name and version.");
        logger.info("To specify a different bom tool type you can specify the project type override.");
    }

}
