package com.synopsys.integration.detect.kotlin.nameversion

import com.synopsys.integration.detector.base.DetectorEvaluation
import com.synopsys.integration.detector.base.DetectorType
import com.synopsys.integration.util.NameVersion
import org.codehaus.plexus.util.StringUtils
import org.slf4j.LoggerFactory
import java.util.*

class DetectorEvaluationNameVersionDecider(private val detectorNameVersionDecider: DetectorNameVersionDecider) {
    private val logger = LoggerFactory.getLogger(DetectorEvaluationNameVersionDecider::class.java)

    fun decideSuggestion(detectorEvaluations: List<DetectorEvaluation>, projectDetector: String?): Optional<NameVersion> {
        val detectorProjectInfo = detectorEvaluations
                .mapNotNull { toProjectInfo(it) }

        val detectorType = preferredDetectorTypeFromString(projectDetector)

        return detectorNameVersionDecider.decideProjectNameVersion(detectorProjectInfo, detectorType)
    }

    private fun preferredDetectorTypeFromString(detectorType: String?): DetectorType? {
        if (detectorType != null && detectorType.isNotBlank()) {
            val castDetectorType = detectorType.toUpperCase().takeIf { DetectorType.POSSIBLE_NAMES.contains(it) }?.let { DetectorType.valueOf(it) }
            if (castDetectorType != null) {
                logger.info("A valid preferred detector type was not provided, deciding project name automatically.")
            }
            return castDetectorType
        }
        return null
    }

    private fun toProjectInfo(detectorEvaluation: DetectorEvaluation): DetectorProjectInfo? {
        var projectName: String? = null
        var projectVersion: String? = null

        if (detectorEvaluation.wasDiscoverySuccessful()) {
            projectName = detectorEvaluation.discovery.projectName
            projectVersion = detectorEvaluation.discovery.projectVersion
        } else if (detectorEvaluation.wasExtractionSuccessful()) {
            projectName = detectorEvaluation.extraction.projectName
            projectVersion = detectorEvaluation.extraction.projectVersion
        }

        if (projectName != null) {
            if (StringUtils.isNotBlank(projectName)) {
                val nameVersion = NameVersion(projectName, projectVersion)
                return DetectorProjectInfo(detectorEvaluation.detectorRule.detectorType, detectorEvaluation.searchEnvironment.depth, nameVersion)
            }
        }

        return null
    }
}
