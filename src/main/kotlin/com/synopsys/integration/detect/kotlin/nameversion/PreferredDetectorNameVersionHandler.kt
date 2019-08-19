package com.synopsys.integration.detect.kotlin.nameversion

import com.synopsys.integration.detector.base.DetectorType
import com.synopsys.integration.util.NameVersion
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

/*
Originally, name version could be decided after all detectors had ran, there was no benefit calculating the name 'on the fly'.
With the introduction of Project Discovery (and Universal Tools) it does make sense to decide the detector project name as it happens.
The moment we have a detector discovery that we know will be our final choice for project name, we can stop further discovery.
Thus, instead of a 'Decider' that decides at the end, we have a handler that takes incoming detector discoveries.
The handler will accept until it has the 'decided' discovery and then rejects all future discoveries.
This allows discovery to run only the minimum amount of discoveries needed.
 */
class PreferredDetectorNameVersionHandler(private val preferredDetectorType: DetectorType) : DetectorNameVersionHandler() {
    private val logger: Logger = LoggerFactory.getLogger(PreferredDetectorNameVersionHandler::class.java)

    override fun willAccept(metadata: DetectorProjectInfoMetadata): Boolean {
        return when (metadata.detectorType) {
            preferredDetectorType -> super.willAccept(metadata)
            else -> false
        }
    }

    override fun accept(projectInfo: DetectorProjectInfo) {
        if (projectInfo.detectorType == preferredDetectorType) {
            super.accept(projectInfo)
        }
    }

    override fun finalDecision(): NameVersionDecision {
        val uniqueDetectorsAtLowestDepth = filterUniqueDetectorsOnly(lowestDepth)

        return when {
            uniqueDetectorsAtLowestDepth.isEmpty() -> PreferredDetectorNotFoundDecision(preferredDetectorType)
            uniqueDetectorsAtLowestDepth.size == 1 -> PreferredDetectorDecision(uniqueDetectorsAtLowestDepth.first())
            else -> TooManyPreferredDetectorTypesFoundDecision(preferredDetectorType)
        }
    }
}
