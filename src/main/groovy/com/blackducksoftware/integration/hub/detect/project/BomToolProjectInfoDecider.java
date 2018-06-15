package com.blackducksoftware.integration.hub.detect.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.project.result.ArbitrarilyChosenProjectInfoResult;
import com.blackducksoftware.integration.hub.detect.project.result.NoUniqueUnchosenProjectInfoResult;
import com.blackducksoftware.integration.hub.detect.project.result.OneUniqueChosenProjectInfoResult;
import com.blackducksoftware.integration.hub.detect.project.result.PreferredMultipleUnchosenProjectInfoResult;
import com.blackducksoftware.integration.hub.detect.project.result.PreferredNotFoundUnchosenProjectInfoResult;
import com.blackducksoftware.integration.hub.detect.project.result.PreferredSingleChosenProjectInfoResult;
import com.blackducksoftware.integration.hub.detect.project.result.ProjectInfoResult;
import com.blackducksoftware.integration.util.NameVersion;

public class BomToolProjectInfoDecider {
    private final Logger logger = LoggerFactory.getLogger(BomToolProjectInfoDecider.class);

    public Optional<NameVersion> decideProjectInfo(final List<BomToolProjectInfo> projectNamePossibilities, final Optional<BomToolType> preferredBomToolType) {
        final ProjectInfoResult chosenBomToolProjectInfo = decideBomToolInfo(projectNamePossibilities, preferredBomToolType);
        chosenBomToolProjectInfo.printDescription(logger);
        if (chosenBomToolProjectInfo.getChosenNameVersion().isPresent()) {
            return chosenBomToolProjectInfo.getChosenNameVersion();
        } else {
            return Optional.empty();
        }
    }

    private ProjectInfoResult decideBomToolInfo(final List<BomToolProjectInfo> projectNamePossibilities, final Optional<BomToolType> preferredBomToolType) {
        if (preferredBomToolType.isPresent()) {
            final List<BomToolProjectInfo> possiblePreferred = projectNamePossibilities.stream()
                    .filter(it -> it.getBomToolType() == preferredBomToolType.get())
                    .collect(Collectors.toList());

            final List<BomToolProjectInfo> lowestDepthPossibilities = projectNamesAtLowestDepth(possiblePreferred);

            if (lowestDepthPossibilities.size() == 0) {
                return new PreferredNotFoundUnchosenProjectInfoResult(preferredBomToolType.get());
            } else if (lowestDepthPossibilities.size() == 1) {
                return new PreferredSingleChosenProjectInfoResult(lowestDepthPossibilities.get(0));
            } else {
                return new PreferredMultipleUnchosenProjectInfoResult(preferredBomToolType.get());
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
                final BomToolType type = singleInstanceLowestDepthBomTools.get(0);
                final BomToolProjectInfo chosen = lowestDepthPossibilities.stream().filter(it -> it.getBomToolType() == type).findFirst().get();
                return new OneUniqueChosenProjectInfoResult(chosen);
            } else if (singleInstanceLowestDepthBomTools.size() > 1) {
                return arbitrarilyDecide(lowestDepthPossibilities, singleInstanceLowestDepthBomTools);
            } else {
                return new NoUniqueUnchosenProjectInfoResult();
            }
        }
    }

    private ProjectInfoResult arbitrarilyDecide(final List<BomToolProjectInfo> possibilities, final List<BomToolType> bomToolOptions) {
        final List<BomToolProjectInfo> arbitraryOptions = possibilities.stream()
                .filter(it -> bomToolOptions.contains(it.getBomToolType()))
                .collect(Collectors.toList());

        final Optional<BomToolProjectInfo> chosen = arbitraryOptions.stream()
                .sorted((o1, o2) -> o1.getNameVersion().getName().compareTo(o2.getNameVersion().getName()))
                .findFirst();

        if (chosen.isPresent()) {
            return new ArbitrarilyChosenProjectInfoResult(chosen.get(), arbitraryOptions);
        } else {
            return new NoUniqueUnchosenProjectInfoResult();
        }
    }

    private List<BomToolProjectInfo> projectNamesAtLowestDepth(final List<BomToolProjectInfo> projectNamePossibilities) {
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
