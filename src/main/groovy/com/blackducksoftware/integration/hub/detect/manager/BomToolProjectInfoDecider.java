package com.blackducksoftware.integration.hub.detect.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.util.NameVersion;

public class BomToolProjectInfoDecider {
    private final Logger logger = LoggerFactory.getLogger(BomToolProjectInfoDecider.class);

    public Optional<NameVersion> decideProjectName(final List<BomToolProjectInfo> projectNamePossibilities, final Optional<BomToolType> preferredBomToolType) {
        final Optional<BomToolProjectInfo> chosenBomToolProjectInfo = decideBomToolInfo(projectNamePossibilities, preferredBomToolType);
        if (chosenBomToolProjectInfo.isPresent()) {
            return Optional.of(chosenBomToolProjectInfo.get().getNameVersion());
        } else {
            return Optional.empty();
        }
    }

    private Optional<BomToolProjectInfo> decideBomToolInfo(final List<BomToolProjectInfo> projectNamePossibilities, final Optional<BomToolType> preferredBomToolType) {

        if (preferredBomToolType.isPresent()) {
            final List<BomToolProjectInfo> possiblePreferred = projectNamePossibilities.stream()
                    .filter(it -> it.getBomToolType() == preferredBomToolType.get())
                    .collect(Collectors.toList());

            final List<BomToolProjectInfo> lowestDepthPossibilities = projectNamesAtLowestDepth(possiblePreferred);

            if (lowestDepthPossibilities.size() == 0) {
                logger.info("No preferred bom tool was found. Project info could not be found in a bom tool.");
                return Optional.empty();
            } else if (lowestDepthPossibilities.size() > 1) {
                logger.info("More than one preferred bom tool was found. Project info could not be found in a bom tool.");
                return Optional.empty();
            } else if (lowestDepthPossibilities.size() == 1) {
                logger.info("Using preferred bom tool.");
                return Optional.of(lowestDepthPossibilities.get(0));
            } else {
                logger.info("An unknown combination of bom tools was found. Project info could not be found in a bom tool.");
                return Optional.empty();
            }
        } else {
            final List<BomToolProjectInfo> lowestDepthPossibilities = projectNamesAtLowestDepth(projectNamePossibilities);

            final Map<BomToolType, Long> lowestDepthTypeCounts = lowestDepthPossibilities.stream()
                    .collect(Collectors.groupingBy(it -> it.getBomToolType(), Collectors.counting()));

            final List<BomToolType> singleInstanceLowestDepthBomTools = lowestDepthTypeCounts.entrySet().stream()
                    .filter(it -> it.getValue() == 1)
                    .map(it -> it.getKey())
                    .collect(Collectors.toList());

            if (singleInstanceLowestDepthBomTools.size() == 1) {
                logger.info("Exactly one unique bom tool was found. Using it for project info.");
                final BomToolType type = singleInstanceLowestDepthBomTools.get(0);
                return lowestDepthPossibilities.stream().filter(it -> it.getBomToolType() == type).findFirst();
            } else if (singleInstanceLowestDepthBomTools.size() > 1) {
                logger.info("Multiple unique bom tools were found. Will decide among them.");
                return arbitrarilyDecide(lowestDepthPossibilities, singleInstanceLowestDepthBomTools);
            } else {
                logger.info("No unique bom tool was found. Project info could not be found in a bom tool.");
                return Optional.empty();
            }
        }

    }

    Optional<BomToolProjectInfo> arbitrarilyDecide(final List<BomToolProjectInfo> possibilities, final List<BomToolType> bomToolOptions) {
        final List<BomToolProjectInfo> arbitraryOptions = possibilities.stream()
                .filter(it -> bomToolOptions.contains(it.getBomToolType()))
                .collect(Collectors.toList());

        final Optional<BomToolProjectInfo> chosen = arbitraryOptions.stream()
                .sorted((o1, o2) -> o1.getNameVersion().getName().compareTo(o2.getNameVersion().getName()))
                .findFirst();

        if (chosen.isPresent()) {
            logger.info("Multiple unique bom tool types were found.");
            logger.info("The following project names were found: ");
            for (final BomToolProjectInfo projectNamePossibility : arbitraryOptions) {
                logger.info(projectNamePossibility.getBomToolType().toString() + ": " + projectNamePossibility.getNameVersion().getName());
            }
            logger.info("Chose to use '" + chosen.get().getBomToolType() + "' for project name and version.");
            logger.info("To specify a different bom tool type you can specify the project type override.");
        }

        return chosen;
    }

    List<BomToolProjectInfo> projectNamesAtLowestDepth(final List<BomToolProjectInfo> projectNamePossibilities){
        final Optional<Integer> lowestDepth = projectNamePossibilities.stream()
                .map(it -> it.getDepth())
                .min(Integer::compare);

        if (lowestDepth.isPresent()) {
            final List<BomToolProjectInfo> lowestDepthPossibilities = projectNamePossibilities.stream()
                    .filter(it -> it.getDepth() == lowestDepth.get())
                    .collect(Collectors.toList());
            return lowestDepthPossibilities;
        } else {
            return new ArrayList<>();
        }

    }
}
