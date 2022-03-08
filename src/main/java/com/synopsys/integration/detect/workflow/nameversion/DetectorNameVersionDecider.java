package com.synopsys.integration.detect.workflow.nameversion;

import static java.util.Collections.emptyList;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.workflow.nameversion.decision.ArbitraryNameVersionDecision;
import com.synopsys.integration.detect.workflow.nameversion.decision.NameVersionDecision;
import com.synopsys.integration.detect.workflow.nameversion.decision.PreferredDetectorDecision;
import com.synopsys.integration.detect.workflow.nameversion.decision.PreferredDetectorNotFoundDecision;
import com.synopsys.integration.detect.workflow.nameversion.decision.TooManyPreferredDetectorTypesFoundDecision;
import com.synopsys.integration.detect.workflow.nameversion.decision.UniqueDetectorDecision;
import com.synopsys.integration.detect.workflow.nameversion.decision.UniqueDetectorNotFoundDecision;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.util.NameVersion;

public class DetectorNameVersionDecider {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Optional<NameVersion> decideProjectNameVersion(List<DetectorProjectInfo> projectNamePossibilities, DetectorType preferredBomToolType) {
        NameVersionDecision nameVersionDecision = decideProjectNameVersionFromDetector(projectNamePossibilities, preferredBomToolType);
        nameVersionDecision.printDescription(logger);
        return nameVersionDecision.getChosenNameVersion();
    }

    private NameVersionDecision decideProjectNameVersionFromDetector(List<DetectorProjectInfo> projectNamePossibilities, DetectorType preferredDetectorType) {
        if (preferredDetectorType != null) {
            List<DetectorProjectInfo> preferredPossibilities = projectNamePossibilities.stream()
                .filter(info -> info.getDetectorType() == preferredDetectorType)
                .collect(Collectors.toList());
            List<DetectorProjectInfo> lowestDepthPossibilities = projectNamesAtLowestDepth(preferredPossibilities);
            List<DetectorProjectInfo> uniqueDetectorsAtLowestDepth = filterUniqueDetectorsOnly(lowestDepthPossibilities);

            if (uniqueDetectorsAtLowestDepth.isEmpty()) {
                return new PreferredDetectorNotFoundDecision(preferredDetectorType);
            } else if (uniqueDetectorsAtLowestDepth.size() == 1) {
                return new PreferredDetectorDecision(uniqueDetectorsAtLowestDepth.get(0));
            } else {
                return new TooManyPreferredDetectorTypesFoundDecision(preferredDetectorType);
            }
        } else {
            List<DetectorProjectInfo> lowestDepthPossibilities = projectNamesAtLowestDepth(projectNamePossibilities);
            List<DetectorProjectInfo> uniqueDetectorsAtLowestDepth = filterUniqueDetectorsOnly(lowestDepthPossibilities);

            if (uniqueDetectorsAtLowestDepth.size() == 1) {
                return new UniqueDetectorDecision(uniqueDetectorsAtLowestDepth.get(0));
            } else if (uniqueDetectorsAtLowestDepth.size() > 1) {
                return decideProjectNameVersionArbitrarily(lowestDepthPossibilities);
            } else {
                return new UniqueDetectorNotFoundDecision();
            }
        }
    }

    private NameVersionDecision decideProjectNameVersionArbitrarily(List<DetectorProjectInfo> allPossibilities) {
        List<DetectorProjectInfo> notGitPossibilities = allPossibilities.stream()
            .filter(info -> info.getDetectorType() != DetectorType.GIT)
            .collect(Collectors.toList());

        List<DetectorProjectInfo> chosenPossibilities;
        if (notGitPossibilities.isEmpty()) {
            chosenPossibilities = allPossibilities;
        } else {
            chosenPossibilities = notGitPossibilities;
        }

        // Kotlin code grabbed the info with the lexicographically-first name
        Function<DetectorProjectInfo, String> getName = detectorProjectInfo -> detectorProjectInfo.getNameVersion().getName();
        DetectorProjectInfo chosen = chosenPossibilities.stream()
            .sorted(Comparator.comparing(getName))
            .collect(Collectors.toList()).get(0);

        if (chosen != null) {
            List<DetectorProjectInfo> otherOptions = chosenPossibilities.stream()
                .filter(info -> info.getDetectorType() != chosen.getDetectorType())
                .collect(Collectors.toList());
            return new ArbitraryNameVersionDecision(chosen.getNameVersion(), chosen, otherOptions);
        }

        return new UniqueDetectorNotFoundDecision();
    }

    //Return only project info whose detector types appear exactly once.
    private List<DetectorProjectInfo> filterUniqueDetectorsOnly(List<DetectorProjectInfo> projectNamePossibilities) {
        Map<DetectorType, List<DetectorProjectInfo>> groupedPossibilities = new HashMap<>();
        for (DetectorProjectInfo possibility : projectNamePossibilities) {
            groupedPossibilities.computeIfAbsent(possibility.getDetectorType(), detectorType -> new LinkedList<>()).add(possibility);
        }

        List<DetectorProjectInfo> unique = new LinkedList<>();
        groupedPossibilities.entrySet().forEach(group -> {
            if (group.getValue().size() == 1) {
                unique.add(group.getValue().get(0));
            }
        });

        return unique;
    }

    private List<DetectorProjectInfo> projectNamesAtLowestDepth(List<DetectorProjectInfo> projectNamePossibilities) {
        List<DetectorProjectInfo> lowestDepthList = projectNamePossibilities.stream()
            .sorted(Comparator.comparing(DetectorProjectInfo::getDepth))
            .collect(Collectors.toList());

        if (!lowestDepthList.isEmpty()) {
            DetectorProjectInfo lowestDepth = lowestDepthList.get(0);
            List<DetectorProjectInfo> allLowest = projectNamePossibilities.stream()
                .filter(info -> info.getDepth() == lowestDepth.getDepth())
                .collect(Collectors.toList());
            return allLowest;
        } else {
            return emptyList();
        }
    }
}