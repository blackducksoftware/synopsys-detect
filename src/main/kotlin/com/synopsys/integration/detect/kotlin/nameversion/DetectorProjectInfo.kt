package com.synopsys.integration.detect.kotlin.nameversion

import com.synopsys.integration.detector.base.DetectorType
import com.synopsys.integration.util.NameVersion

class DetectorProjectInfo(val detectorType: DetectorType, val depth: Int, val nameVersion: NameVersion) {
    constructor(metadata: DetectorProjectInfoMetadata, nameVersion: NameVersion) : this(metadata.detectorType, metadata.depth, nameVersion)
}


