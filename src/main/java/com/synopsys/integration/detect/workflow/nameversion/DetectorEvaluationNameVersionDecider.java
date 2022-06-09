package com.synopsys.integration.detect.workflow.nameversion;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.tool.detector.report.DetectorDirectoryReport;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.util.NameVersion;

public class DetectorEvaluationNameVersionDecider {
    private final DetectorNameVersionDecider detectorNameVersionDecider;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public DetectorEvaluationNameVersionDecider(DetectorNameVersionDecider detectorNameVersionDecider) {
        this.detectorNameVersionDecider = detectorNameVersionDecider;
    }

    public Optional<NameVersion> decideSuggestion(List<DetectorDirectoryReport> reports, String projectDetector) {
        List<DetectorProjectInfo> detectorProjectInfoList = convertEvaluationToProjectInfo(reports);

        DetectorType detectorType = preferredDetectorTypeFromString(projectDetector); //TODO (detectors): this should not be done here, should be done in option factory

        return detectorNameVersionDecider.decideProjectNameVersion(detectorProjectInfoList, detectorType);
    }

    List<DetectorProjectInfo> convertEvaluationToProjectInfo(List<DetectorDirectoryReport> reports) {
        List<DetectorProjectInfo> projectInfos = new ArrayList<>();

        reports.forEach(report -> {
            report.getExtractedDetectors().forEach(extracted -> {
                Extraction extraction = extracted.getExtractedDetectable().getExtraction();
                if (StringUtils.isNotBlank(extraction.getProjectName())) {
                    NameVersion nameVersion = new NameVersion(extraction.getProjectName(), extraction.getProjectVersion());
                    projectInfos.add(new DetectorProjectInfo(extracted.getRule().getDetectorType(), report.getDepth(), nameVersion));
                }
            });
        });

        return projectInfos;
    }

    private DetectorType preferredDetectorTypeFromString(String detectorType) {
        DetectorType castDetectorType = null;
        if (StringUtils.isNotBlank(detectorType)) {
            String detectorTypeUppercase = detectorType.toUpperCase();
            if (DetectorType.getPossibleNames().contains(detectorTypeUppercase)) {
                castDetectorType = DetectorType.valueOf(detectorTypeUppercase);
            }
        }
        // In Kotlin this check was to see if castDetectorType != null but that doesn't make any sense...
        if (castDetectorType == null) {
            logger.debug("A valid preferred detector type was not provided, deciding project name automatically.");
        }
        return castDetectorType;
    }
}