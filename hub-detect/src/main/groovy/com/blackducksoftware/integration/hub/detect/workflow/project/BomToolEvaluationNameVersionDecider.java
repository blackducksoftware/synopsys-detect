package com.blackducksoftware.integration.hub.detect.workflow.project;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.detector.DetectorType;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorEvaluation;
import com.synopsys.integration.util.NameVersion;

public class BomToolEvaluationNameVersionDecider {
    private final Logger logger = LoggerFactory.getLogger(BomToolEvaluationNameVersionDecider.class);

    private final BomToolNameVersionDecider bomToolNameVersionDecider;

    public BomToolEvaluationNameVersionDecider(BomToolNameVersionDecider bomToolNameVersionDecider) {
        this.bomToolNameVersionDecider = bomToolNameVersionDecider;
    }

    public Optional<NameVersion> decideSuggestion(final List<DetectorEvaluation> detectorEvaluations, String projectBomTool) {
        DetectorType preferredBomToolType = null;
        if (StringUtils.isNotBlank(projectBomTool)) {
            final String projectBomToolFixed = projectBomTool.toUpperCase();
            if (!DetectorType.POSSIBLE_NAMES.contains(projectBomToolFixed)) {
                logger.info("A valid preferred bom tool type was not provided, deciding project name automatically.");
            } else {
                preferredBomToolType = DetectorType.valueOf(projectBomToolFixed);
            }
        }

        final List<BomToolProjectInfo> allBomToolProjectInfo = transformIntoProjectInfo(detectorEvaluations);
        return bomToolNameVersionDecider.decideProjectNameVersion(allBomToolProjectInfo, preferredBomToolType);
    }

    private List<BomToolProjectInfo> transformIntoProjectInfo(final List<DetectorEvaluation> detectorEvaluations) {
        return detectorEvaluations.stream()
                   .filter(it -> it.wasExtractionSuccessful())
                   .filter(it -> it.getExtraction().projectName != null)
                   .map(it -> {
                       final NameVersion nameVersion = new NameVersion(it.getExtraction().projectName, it.getExtraction().projectVersion);
                       final BomToolProjectInfo possibility = new BomToolProjectInfo(it.getDetector().getDetectorType(), it.getEnvironment().getDepth(), nameVersion);
                       return possibility;
                   })
                   .collect(Collectors.toList());
    }
}
