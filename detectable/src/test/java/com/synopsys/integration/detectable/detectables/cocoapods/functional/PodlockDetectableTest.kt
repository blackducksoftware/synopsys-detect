package com.synopsys.integration.detectable.detectables.cocoapods.functional

import com.synopsys.integration.bdio.model.Forge
import com.synopsys.integration.detectable.Detectable
import com.synopsys.integration.detectable.DetectableEnvironment
import com.synopsys.integration.detectable.Extraction
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert

class PodlockDetectableTest : DetectableFunctionalTest("podlock") {
    override fun setup() {
        addFileFromResource("Podfile.lock", "/cocoapods/simplePodfile.lock")
    }

    override fun create(environment: DetectableEnvironment): Detectable {
        return detectableFactory.createPodLockDetectable(environment)
    }

    override fun assert(extraction: Extraction) {
        val graphAssert = NameVersionGraphAssert(Forge.COCOAPODS, extraction.codeLocations.first().dependencyGraph)
        graphAssert.hasRootSize(3)
        graphAssert.hasRootDependency("Allihoopa-macOS", "1.1.0");
        graphAssert.hasRootDependency("Mapbox-macOS-SDK", "0.4.1");
        graphAssert.hasRootDependency("RepliesSDK-macOS", "0.2.22");

        graphAssert.hasParentChildRelationship("Allihoopa-macOS", "1.1.0", "AllihoopaCore", "1.1.0")
    }
}