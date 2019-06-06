package com.synopsys.integration.detect.kotlin.project

import com.synopsys.integration.detector.base.DetectorType
import com.synopsys.integration.util.NameVersion
import org.slf4j.LoggerFactory
import java.util.*

class DetectorNameVersionDecider {
    private val logger = LoggerFactory.getLogger(DetectorNameVersionDecider::class.java)

    fun decideProjectNameVersion(projectNamePossibilities: List<DetectorProjectInfo>, preferredBomToolType: DetectorType?): Optional<NameVersion> {
        val nameVersionDecision = decideProjectNameVersionFromDetector(projectNamePossibilities, preferredBomToolType)
        nameVersionDecision.printDescription(logger)
        return nameVersionDecision.chosenNameVersion
    }

    private fun decideProjectNameVersionFromDetector(projectNamePossibilities: List<DetectorProjectInfo>, preferredDetectorType: DetectorType?): NameVersionDecision {
        if (preferredDetectorType != null) {
            val preferredPossibilities = projectNamePossibilities.filter { it.detectorType == preferredDetectorType }
            val lowestDepthPossibilities = projectNamesAtLowestDepth(preferredPossibilities)
            val uniqueDetectorsAtLowestDepth = filterUniqueDetectorsOnly(lowestDepthPossibilities)

            return when {
                uniqueDetectorsAtLowestDepth.isEmpty() -> PreferredDetectorNotFoundDecision(preferredDetectorType)
                uniqueDetectorsAtLowestDepth.size == 1 -> PreferredDetectorDecision(uniqueDetectorsAtLowestDepth.first())
                else -> TooManyPreferredDetectorTypesFoundDecision(preferredDetectorType)
            }
        } else {
            val lowestDepthPossibilities = projectNamesAtLowestDepth(projectNamePossibilities)
            val uniqueDetectorsAtLowestDepth = filterUniqueDetectorsOnly(lowestDepthPossibilities)

            return when {
                uniqueDetectorsAtLowestDepth.size == 1 -> UniqueDetectorDecision(uniqueDetectorsAtLowestDepth.first())
                uniqueDetectorsAtLowestDepth.size > 1 -> decideProjectNameVersionArbitrarily(lowestDepthPossibilities)
                else -> UniqueDetectorNotFoundDecision()
            }
        }
    }

    private fun decideProjectNameVersionArbitrarily(arbitraryPossibilities: List<DetectorProjectInfo>): NameVersionDecision {
        val notGit = arbitraryPossibilities.filter { it.detectorType != DetectorType.GIT }

        val actualOptions = when {
            notGit.isEmpty() -> arbitraryPossibilities
            else -> notGit
        }

        val chosen = actualOptions.minBy { it.nameVersion.name }
        chosen?.let {
            val otherOptions = actualOptions.filter { it.detectorType != chosen.detectorType }
            return ArbitraryNameVersionDecision(Optional.of(chosen.nameVersion), chosen, otherOptions.toList())
        }

        return UniqueDetectorNotFoundDecision()
    }

    //Return only project info whose detector types appear exactly once.
    private fun filterUniqueDetectorsOnly(projectNamePossibilities: List<DetectorProjectInfo>): List<DetectorProjectInfo> {
        val grouped = projectNamePossibilities.groupBy { it.detectorType }
        val uniqueGroups = grouped.filter { it.value.size == 1 }
        return uniqueGroups.flatMap { it.value }
    }

    private fun projectNamesAtLowestDepth(projectNamePossibilities: List<DetectorProjectInfo>): List<DetectorProjectInfo> {
        val lowestDepth = projectNamePossibilities.minBy { it.depth }

        lowestDepth?.let {
            val allLowest = projectNamePossibilities.filter { it.depth == lowestDepth.depth }
            return allLowest.toList()
        }

        return emptyList()
    }
}
